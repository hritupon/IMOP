package com.opinion.services.impl;

import com.google.inject.Inject;
import com.opinion.repository.RedisSourceRepository;
import com.opinion.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hritupon on 9/18/2015.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RedisSourceRepository redisSourceRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Inject
    public AuthenticationServiceImpl(RedisSourceRepository redisSourceRepository){
        this.redisSourceRepository=redisSourceRepository;
    }

    @Override
    public String validateUser(String userName) throws Exception {
        return null;
    }

    @Override
    public String getPassword(String userName) throws Exception{
        return "";
    }
}
