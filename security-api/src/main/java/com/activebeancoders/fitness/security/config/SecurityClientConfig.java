package com.activebeancoders.fitness.security.config;

import com.activebeancoders.fitness.security.api.AuthenticationService;
import com.activebeancoders.fitness.security.api.SecurityClientController;
import com.activebeancoders.fitness.security.api.TokenValidationService;
import com.activebeancoders.fitness.security.infrastructure.AuthenticationTokenHttpInvokerRequestExecutor;
import com.activebeancoders.fitness.security.api.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Spring configuration for security-api.
 *
 * @author Dan Barrese
 */
@Configuration
@PropertySource(value = "classpath:/security-api.properties", ignoreResourceNotFound = false)
public class SecurityClientConfig {

    @Autowired
    private AuthenticationTokenHttpInvokerRequestExecutor executor;

    @Value("${external-url.security-service}")
    private String securityServiceUrl;

    @Bean
    public SecurityService remoteSecurityService() {
        HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
        proxy.setServiceUrl(securityServiceUrl + "/public/securityService.http");
        proxy.setServiceInterface(SecurityService.class);
        proxy.setHttpInvokerRequestExecutor(executor);
        proxy.afterPropertiesSet();
        return (SecurityService) proxy.getObject();
    }

    @Bean
    public AuthenticationService remoteAuthenticationService() {
        HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
        proxy.setServiceUrl(securityServiceUrl + "/public/" + SecurityClientController.getAuthenticateEndpointFromRemoteMethodCall());
        proxy.setServiceInterface(AuthenticationService.class);
        proxy.setHttpInvokerRequestExecutor(executor);
        proxy.afterPropertiesSet();
        return (AuthenticationService) proxy.getObject();
    }

    @Bean
    public TokenValidationService remoteTokenValidationService() {
        HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
        proxy.setServiceUrl(securityServiceUrl + "/public/" + SecurityClientController.getTokenValidationEndpointFromRemoteMethodCall());
        proxy.setServiceInterface(TokenValidationService.class);
        proxy.setHttpInvokerRequestExecutor(executor);
        proxy.afterPropertiesSet();
        return (TokenValidationService) proxy.getObject();
    }

}
