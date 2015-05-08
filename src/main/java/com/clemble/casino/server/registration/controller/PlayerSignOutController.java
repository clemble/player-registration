package com.clemble.casino.server.registration.controller;

import com.clemble.casino.registration.RegistrationWebMapping;
import com.clemble.casino.registration.service.PlayerSignOutService;
import com.clemble.casino.server.ExternalController;
import com.clemble.casino.server.security.PlayerTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mavarazy on 1/14/15.
 */
@RestController
public class PlayerSignOutController implements PlayerSignOutService, ExternalController {

    final private Logger LOG = LoggerFactory.getLogger(PlayerSignOutController.class);

    final private String host;
    final private PlayerTokenUtils tokenUtils;

    public PlayerSignOutController(String host, PlayerTokenUtils tokenUtils) {
        this.host = host;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void signOut() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = RegistrationWebMapping.REGISTRATION_SIGN_OUT)
    public void signOut(HttpServletResponse signOut) {
        // Step 1. Removing cookies
        tokenUtils.signOut(signOut);
        // Step 2. Redirect to parent
        try {
            signOut.sendRedirect(host);
        } catch (IOException e) {
            // This is wrapped in order to prevent log filtering in papertrail
            LOG.error("Error, while sending redirect", e);
            throw new RuntimeException(e);
        }
    }

}
