package com.activebeancoders.fitness.security.service;

import com.activebeancoders.fitness.security.api.SecurityService;

/**
 * @author Dan Barrese
 */
public class SecurityServiceImpl implements SecurityService {

    @Override
    public String sayHello() {
        return "hello from a secured endpoint on security service";
    }

}