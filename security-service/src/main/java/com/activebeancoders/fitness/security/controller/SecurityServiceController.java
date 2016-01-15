package com.activebeancoders.fitness.security.controller;

import com.activebeancoders.fitness.security.api.AuthenticationService;
import com.activebeancoders.fitness.security.api.SecurityClientController;
import com.activebeancoders.fitness.security.api.SecurityService;
import com.activebeancoders.fitness.security.api.TokenValidationService;
import com.activebeancoders.fitness.security.domain.CurrentlyLoggedInUser;
import com.activebeancoders.fitness.security.domain.DomainUser;
import com.activebeancoders.fitness.security.domain.DomainUserCredentials;
import com.activebeancoders.fitness.security.exception.Http401Error;
import com.activebeancoders.fitness.security.exception.Http500Error;
import com.activebeancoders.fitness.security.infrastructure.AuthenticationDao;
import com.activebeancoders.fitness.security.infrastructure.AuthenticationWithToken;
import com.activebeancoders.fitness.security.infrastructure.TokenResponse;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * @author Dan Barrese
 */
@Configuration
@RestController
public class SecurityServiceController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private SecurityService securityService;
    @Autowired
    private TokenValidationService tokenValidationService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationDao authenticationDao;

    public SecurityServiceController() {
    }

    // Sample method showing off @CurrentlyLoggedInUser
    @RequestMapping("/user")
    public Map<String, String> user(@CurrentlyLoggedInUser DomainUser domainUser) {
        return Collections.singletonMap("username", domainUser.getUsername());
    }

    @RequestMapping(value = SecurityClientController.URL_VALIDATE_AUTH_TOKEN, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void validateAuthToken(@RequestHeader(value = "X-Auth-Token") final String token) {
        Optional<String> sessionToken = Optional.fromNullable(token);
        tokenValidationService.validateToken(sessionToken);
    }

    @RequestMapping(value = SecurityClientController.URL_AUTH_USER_CREDS, method = RequestMethod.POST)
    public TokenResponse authenticateUserCredentials(@RequestHeader(value = "X-Auth-Username") final String username,
                                              @RequestHeader(value = "X-Auth-Password") final String plaintextPassword) {
        // TODO: set cookie on response?  Is that more secure?
        try {
            AuthenticationWithToken authentication = authenticationService.authenticate(username, plaintextPassword);
            return new TokenResponse(authentication.getToken());
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            authenticationDao.clearCurrentSessionAuthentication();
            throw new Http500Error();
        } catch (AuthenticationException authenticationException) {
            authenticationDao.clearCurrentSessionAuthentication();
            log.info("Unauthorized: '{}'", authenticationException.getMessage());
            throw new Http401Error();
        }
    }

    @RequestMapping(value = SecurityClientController.URL_LOGOUT, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void logout(@RequestHeader(value = "X-Auth-Token") final String token) {
        Optional<String> sessionToken = Optional.fromNullable(token);
        tokenValidationService.invalidateToken(sessionToken);
    }

    // TODO: should this be publicly accessible?  Seems like easy DOS opportunity.
    @RequestMapping(value = "/public/user/create", method = RequestMethod.POST)
    public DomainUser createUserAccount(
            @RequestHeader(value = "username") final String username,
            @RequestHeader(value = "plaintextPassword") final String plaintextPassword) {
        DomainUserCredentials domainUserCredentials = new DomainUserCredentials(username, plaintextPassword);
        return securityService.createUserAccount(domainUserCredentials);
    }

}
