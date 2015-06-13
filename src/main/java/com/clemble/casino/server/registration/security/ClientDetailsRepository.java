package com.clemble.casino.server.registration.security;

import java.util.List;

import com.clemble.casino.security.ClientDetails;

public interface ClientDetailsRepository {

    ClientDetails findOne(String client);

    List<ClientDetails> findByPlayer(String player);

}
