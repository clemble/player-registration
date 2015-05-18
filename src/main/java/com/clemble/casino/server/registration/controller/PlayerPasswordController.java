package com.clemble.casino.server.registration.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.error.ClembleErrorCode;
import com.clemble.casino.error.ClembleException;
import com.clemble.casino.registration.PlayerPasswordChangeRequest;
import com.clemble.casino.registration.PlayerPasswordResetRequest;
import com.clemble.casino.registration.PlayerPasswordRestoreRequest;
import com.clemble.casino.registration.service.PlayerPasswordService;
import com.clemble.casino.server.ServerController;
import com.clemble.casino.server.registration.service.PasswordResetTokenService;
import com.clemble.casino.server.registration.service.ServerPlayerCredentialManager;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.clemble.casino.registration.RegistrationWebMapping.*;

/**
 * Created by mavarazy on 2/2/15.
 */
@RestController
public class PlayerPasswordController implements PlayerPasswordService, ServerController {

    final private PasswordResetTokenService tokenService;
    final private ServerPlayerCredentialManager credentialManager;

    public PlayerPasswordController(
            PasswordResetTokenService tokenService,
            ServerPlayerCredentialManager credentialManager) {
        this.tokenService = tokenService;
        this.credentialManager = credentialManager;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = RESTORE_PASSWORD, produces = WebMapping.PRODUCES)
    public boolean restore(@Valid @RequestBody PlayerPasswordRestoreRequest restoreRequest) {
        // Step 1. Looking up player credentials
        String player = credentialManager.findPlayerByEmail(restoreRequest.getEmail());
        // Step 2. Checking player does exists
        if (player == null)
            throw ClembleException.fromError(ClembleErrorCode.EmailInvalid);
        // Step 3. Generate and send email token
        tokenService.generateAndSend(player);
        // Step 4. Consider that everything done
        return true;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = RESET_PASSWORD, produces = WebMapping.PRODUCES)
    public boolean reset(@Valid @RequestBody PlayerPasswordResetRequest request) {
        // Step 1. Checking player & token match
        String player = tokenService.verify(request.getToken());
        // Step 2. If they match update player password
        if (player != null) {
            // Case 1. Player was successfully fetched, and token was verified
            return credentialManager.update(player, request.getPassword()) != null;
        } else {
            // Case 2. Player or token illegal
            return false;
        }
    }

    @Override
    public boolean change(@Valid @RequestBody PlayerPasswordChangeRequest changeRequest) {
        throw new UnsupportedOperationException();
    }


    @RequestMapping(method = RequestMethod.POST, value = CHANGE_PASSWORD, produces = WebMapping.PRODUCES)
    public boolean change(@CookieValue("player") String player, @Valid @RequestBody PlayerPasswordChangeRequest changeRequest) {
        // Step 1. Verifying password matches
        boolean passwordMatch = credentialManager.verifyByPassword(player, changeRequest.getOldPassword());
        if (!passwordMatch)
            throw ClembleException.fromError(ClembleErrorCode.PasswordIncorrect);
        // Step 2. Updating password
        return credentialManager.update(player, changeRequest.getPassword()) != null;
    }

}
