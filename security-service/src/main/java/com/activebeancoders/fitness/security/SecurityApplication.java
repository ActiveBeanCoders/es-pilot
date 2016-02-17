package com.activebeancoders.fitness.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Main class for running the security service.
 *
 * @author Dan Barrese
 */
@EnableWebMvc
@EnableWebMvcSecurity
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

}
