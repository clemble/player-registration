package com.clemble.casino.server.registration.security;

import java.util.List;

import com.clemble.casino.security.ClientDetails;

public interface ClientDetailsRepository {

    public ClientDetails findOne(String client);

    public List<ClientDetails> findByPlayer(String player);

}
