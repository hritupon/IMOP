package com.opinion.services.impl;


import com.google.inject.Inject;
import com.opinion.models.AddFollowerRequest;
import com.opinion.models.LoginRequest;
import com.opinion.models.UserAddRequest;
import com.opinion.repository.RedisSourceRepository;
import com.opinion.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by w7 on 5/7/2015.
 */
public class UserServiceImpl implements UserService {
    private final RedisSourceRepository redisSourceRepository;

    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);

    @Inject
    public UserServiceImpl(RedisSourceRepository redisSourceRepository) {
        this.redisSourceRepository = redisSourceRepository;
    }

    public void getUserDetails(){

    }

    @Override
    public boolean checkValidUser(String userName) {
        return redisSourceRepository.checkValidUser(userName);
    }

    @Override
    public String getUserId(String userName){
        return redisSourceRepository.getUserId(userName);
    }

    @Override
    public void addFollower(AddFollowerRequest addFollowerRequest) {
        redisSourceRepository.addFollower(addFollowerRequest);
    }

    @Override
    public Map<String,String> login(LoginRequest loginRequest) {
        return redisSourceRepository.login(loginRequest);
    }

    @Override
    public String getNextUserID(){
        return redisSourceRepository.getNextUserID();
    }

    @Override
    public String addUser(String userID,UserAddRequest userAddRequest){
        redisSourceRepository.addUserIDMap(userID,userAddRequest);
        return redisSourceRepository.addUser(userID,userAddRequest);
    }

}
