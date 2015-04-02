package com.clemble.casino.server.registration.spring;

import com.clemble.casino.server.spring.WebBootSpringConfiguration;
import com.clemble.casino.server.spring.common.ClembleBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 3/30/15.
 */
@Configuration
@Import({
    WebBootSpringConfiguration.class,
    RegistrationSpringConfiguration.class
})
public class RegistrationApplication implements ClembleBootApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RegistrationApplication.class, args);
    }

}
