package com.opinion.services;

/**
 * Created by hritupon on 9/18/2015.
 */
public interface AuthenticationService {

    String validateUser(String userName) throws Exception;
    String getPassword(String userName) throws Exception;
}
