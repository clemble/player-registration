package com.clemble.casino.server.registration.spring;

import com.clemble.casino.server.registration.security.ClembleConsumerDetailsService;
import com.clemble.casino.server.registration.security.SimpleClembleConsumerDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.server.spring.common.SpringConfiguration;

@Configuration
@Import(value = {
    OAuthSpringConfiguration.SimpleOAuthSpringConfiguration.class
})
public class OAuthSpringConfiguration implements SpringConfiguration {

    @Configuration
    public static class SimpleOAuthSpringConfiguration implements SpringConfiguration {

        @Bean
        public ClembleConsumerDetailsService clembleConsumerDetailsService() {
            return new SimpleClembleConsumerDetailsService();
        }

    }

}
