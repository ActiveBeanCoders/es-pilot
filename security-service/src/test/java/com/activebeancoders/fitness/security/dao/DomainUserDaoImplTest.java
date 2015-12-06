package com.activebeancoders.fitness.security.dao;

import com.activebeancoders.fitness.security.config.SecurityServiceConfig;
import com.activebeancoders.fitness.security.domain.DomainUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityServiceConfig.class})
public class DomainUserDaoImplTest {

    @Autowired
    DomainUserDaoImpl domainUserDaoImpl;

    @Test
    public void testSave() throws Exception {
        String username = "user";
        DomainUser domainUser = new DomainUser();
        domainUser.setUsername(username);
        domainUser.setNickname("yoyo");
        domainUser.setSpaceDelimitedRoles("ROLE_DOMAIN_USER");
        domainUserDaoImpl.save(domainUser);
        DomainUser saved = domainUserDaoImpl.findByUsername(username);
        Assert.assertNotNull(saved);
    }

}

