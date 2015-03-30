package com.clemble.casino.server.registration.service;

import com.clemble.casino.server.registration.spring.RegistrationSpringConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by mavarazy on 8/16/14.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RegistrationSpringConfiguration.class })
public class GravatarServiceTest {

    @Test
    public void testUrl() {
        String url = GravatarService.toRedirect("example@exampl.com");
        Assert.assertEquals(url, "http://www.gravatar.com/avatar/a1531bbe8d2c7214ff96d9f8206e6a36");
    }

}
