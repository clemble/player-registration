package com.clemble.casino.server.registration.service;

import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.server.registration.ServerPlayerCredential;
import com.clemble.casino.server.registration.repository.ServerPlayerCredentialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by mavarazy on 2/2/15.
 */
public class ServerPlayerCredentialManager {

    final private PasswordEncoder passwordEncoder;
    final private ServerPlayerCredentialRepository credentialRepository;

    public ServerPlayerCredentialManager(
        PasswordEncoder passwordEncoder,
        ServerPlayerCredentialRepository credentialRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.credentialRepository = credentialRepository;
    }

    public ServerPlayerCredential save(String player, String email, String nickName, String password) {
        // Step 1. Generating Server player credentials
        ServerPlayerCredential playerCredential = new ServerPlayerCredential(
            player,
            email,
            nickName,
            passwordEncoder.encode(password)
        );
        // Step 2. Saving Player credentials in repository
        return credentialRepository.save(playerCredential);
    }

    public ServerPlayerCredential update(String player, String password) {
        if (!credentialRepository.exists(player))
            return null;
        // Step 1. Looking up player credentials
        ServerPlayerCredential playerCredential = credentialRepository.findOne(player);
        // Step 2. Creating new player credentials
        ServerPlayerCredential newCredentials = new ServerPlayerCredential(
            playerCredential.getPlayer(),
            playerCredential.getEmail(),
            playerCredential.getNickName(),
            passwordEncoder.encode(password)
        );
        // Step 3. Saving new player credentials
        return credentialRepository.save(newCredentials);
    }

    public String verifyByCredentials(PlayerCredential credential) {
        // Step 1. Looking up player credentials
        ServerPlayerCredential playerCredential = credentialRepository.findByEmail(credential.getEmail());
        // Step 2. Checking player matches
        return playerCredential == null
            ? null
            : passwordEncoder.matches(credential.getPassword(), playerCredential.getHash()) ? playerCredential.getPlayer() : null;
    }

    public boolean verifyByPassword(String player, String password) {
        // Step 1. Looking up player credentials
        ServerPlayerCredential credentials = credentialRepository.findOne(player);
        // Step 2. Checking password matches
        if (password == null && credentials == null)
            return true;
        else
            return password != null &&
                    credentials != null &&
                    passwordEncoder.matches(password, credentials.getHash());
    }

    public String findPlayerByEmail(String email) {
        // Step 1. Fetching player credentials
        ServerPlayerCredential playerCredential = credentialRepository.findByEmail(email);
        // Step 2. Return player associated with the email
        return playerCredential != null ? playerCredential.getPlayer() : null;
    }

    public boolean existsByNickname(String nickName) {
        return credentialRepository.findByNickName(nickName) != null;
    }

    public boolean existsByEmail(String email) {
        return credentialRepository.findByEmail(email) != null;
    }

    private String verify(ServerPlayerCredential credentials, String password) {
        if (credentials == null) {
            return password == null ? credentials.getPlayer() : null;
        } else {
            return passwordEncoder.matches(password, credentials.getHash()) ? credentials.getPlayer() : null;
        }
    }
}
