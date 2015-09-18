package com.opinion.services;

import com.opinion.models.AddFollowerRequest;
import com.opinion.models.LoginRequest;
import com.opinion.models.UserAddRequest;

import java.util.Map;

/**
 * Created by w7 on 5/7/2015.
 */
public interface UserService {
    void getUserDetails();
    boolean checkValidUser(String userId);
    String getNextUserID();
    String addUser(String userID,UserAddRequest userAddRequest);
    String getUserId(String userName);
    void addFollower(AddFollowerRequest addFollowerRequest);
    Map<String,String> login(LoginRequest loginRequest);
}
