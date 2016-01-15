package com.activebeancoders.fitness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author Dan Barrese
 */
@Configuration
@PropertySource(value = "classpath:/hibernate-service.properties", ignoreResourceNotFound = false)
@PropertySource(value = "file:${user.home}/activebeancoders/global.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${user.home}/activebeancoders/hibernate-service.properties", ignoreResourceNotFound = true)
public class HibernateServiceConfig {

    //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}

