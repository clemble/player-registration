package com.clemble.casino.server.registration.controller;

import static com.clemble.casino.registration.RegistrationWebMapping.*;
import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.error.ClembleError;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.registration.service.PlayerRegistrationService;
import com.clemble.casino.server.event.email.SystemEmailAddedEvent;
import com.clemble.casino.server.event.player.SystemPlayerImageChangedEvent;
import com.clemble.casino.server.event.player.SystemPlayerProfileRegisteredEvent;
import com.clemble.casino.server.registration.PlayerKeyGenerator;
import com.clemble.casino.server.registration.service.GravatarService;
import com.clemble.casino.server.registration.service.ServerPlayerCredentialManager;
import com.clemble.casino.server.security.PlayerTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.clemble.casino.error.ClembleErrorCode;
import com.clemble.casino.error.ClembleException;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.PlayerRegistrationRequest;
import com.clemble.casino.server.ServerController;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.WebMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class PlayerRegistrationController implements PlayerRegistrationService, ServerController {
    // !!!TODO need a safe restoration process for all Registrations not only for login!!!

    final private PlayerTokenUtils tokenUtils;
    final private PlayerKeyGenerator playerKeyGenerator;
    final private ServerPlayerCredentialManager credentialManager;
    final private SystemNotificationService notificationService;

    public PlayerRegistrationController(
        ServerPlayerCredentialManager credentialManager,
        PlayerTokenUtils tokenUtils,
        PlayerKeyGenerator playerKeyGenerator,
        SystemNotificationService notificationService) {
        this.tokenUtils = checkNotNull(tokenUtils);
        this.playerKeyGenerator = checkNotNull(playerKeyGenerator);
        this.credentialManager = credentialManager;
        this.notificationService = checkNotNull(notificationService);
    }

    @Override
    public String login(PlayerCredential credentials) {
        if (!credentialManager.existsByEmail(credentials.getEmail())){
            ClembleError failure = ClembleError.withFieldError("email", ClembleErrorCode.EmailNotRegistered);
            throw ClembleException.fromDescription(failure);
        }
        // Step 1. Processing login request
        String player = credentialManager.verifyByCredentials(credentials);
        if (player == null) {
            ClembleError failure = ClembleError.withFieldError("password", ClembleErrorCode.PasswordIncorrect);
            throw ClembleException.fromDescription(failure);
        }
        return player;
    }

    @RequestMapping(method = RequestMethod.POST, value = REGISTRATION_LOGIN, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public String httpLogin(@Valid @RequestBody PlayerCredential loginRequest, HttpServletResponse response) {
        // Step 1. Processing login request
        String player = login(loginRequest);
        // Step 2. Updating HttpResponse
        tokenUtils.updateResponse(player, response);
        return player;
    }

    @Override
    public String register(final PlayerRegistrationRequest registrationRequest) {
        // Step 1.1 Checking user not already exists
        String registeredPlayer = credentialManager.findPlayerByEmail(registrationRequest.getEmail());
        if (registeredPlayer != null) {
            PlayerCredential credentials = registrationRequest.toCredentials();
            // Step 1.2. Verify password matches
            String player = login(credentials);
            // Step 1.3. Returning validated profile
            return player;
        }
        // Step 2. Creating appropriate PlayerProfile
        String player = playerKeyGenerator.generate();
        // Step 3. Adding initial fields to PlayerProfile
        PlayerProfile normalizedProfile = registrationRequest.toProfileWithPlayer(player);
        if (credentialManager.existsByNickname(normalizedProfile.getNickName()))
            throw ClembleException.fromDescription(ClembleError.withFieldError("nickName", ClembleErrorCode.NickOccupied));
        // Step 4. Create new credentials
        credentialManager.save(player, registrationRequest.getEmail(), normalizedProfile.getNickName(), registrationRequest.getPassword());
        // Step 5. Generating default image redirect
        String imageRedirect = GravatarService.toRedirect(registrationRequest.getEmail());
        notificationService.send(new SystemPlayerImageChangedEvent(player, imageRedirect, imageRedirect + "?s=48"));

        // Step 6. Notifying system of new user
        notificationService.send(new SystemPlayerProfileRegisteredEvent(player, normalizedProfile));
        // Step 7.1. Creating email added event
        notificationService.send(new SystemEmailAddedEvent(player, registrationRequest.getEmail(), false));
        // Step 8. All done returning response
        return player;
    }

    @RequestMapping(method = RequestMethod.POST, value = REGISTRATION_PROFILE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public String httpRegister(@Valid @RequestBody final PlayerRegistrationRequest registrationRequest, HttpServletResponse response) {
        String player = register(registrationRequest);
        tokenUtils.updateResponse(player, response);
        return player;
    }

}
