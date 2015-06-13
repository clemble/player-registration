package com.clemble.casino.server.registration.repository;

import com.clemble.casino.server.registration.ServerPlayerCredential;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.clemble.casino.registration.PlayerCredential;

public interface ServerPlayerCredentialRepository extends MongoRepository<ServerPlayerCredential, String> {

    ServerPlayerCredential findByEmail(String email);

    ServerPlayerCredential findByEmailOrNickName(String email, String nickName);

    ServerPlayerCredential findByNickName(String nickName);
}
