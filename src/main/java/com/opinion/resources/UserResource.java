package com.opinion.resources;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.opinion.models.*;
import com.opinion.services.IngestionService;
import com.opinion.services.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by hritupon on 23/5/15.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserService userService;
    private IngestionService ingestionService;
    @Inject
    public UserResource(UserService userService,IngestionService ingestionService) {
        this.userService=userService;
        this.ingestionService=ingestionService;
    }

    @POST
    @Path("/add")
    public UserAddResponse addUser(UserAddRequest userAddRequest)throws Exception{
        UserAddResponse userAddResponse=new UserAddResponse();
        userAddResponse.setUserName(userAddRequest.getUserName());
        if(userService.checkValidUser(userAddRequest.getUserName())){
            userAddResponse.setStatusCode("ALREADY_EXISTS");
            return userAddResponse;
        }else{
            String userID=userService.getNextUserID();
            String resp=userService.addUser(userID,userAddRequest);
            userAddResponse.setId(userID);
            userAddResponse.setStatusCode(resp);
        }
        return userAddResponse;
    }
    @GET
    @Path("/valid")
    public Boolean checkIfExists(@QueryParam("user") String userName){
        return userService.checkValidUser(userName);
    }

    @GET
    @Path("/userId")
    public String getUserId(@QueryParam("user") String userName){
        return userService.getUserId(userName);
    }

    @POST
    @Path("/addfollower")
    public AddFollowerResponse addFollower(AddFollowerRequest addFollowerRequest){
        userService.addFollower(addFollowerRequest);
        AddFollowerResponse addFollowerResponse=new AddFollowerResponse();
        return  addFollowerResponse;
    }

    @POST
    @Path("/login")
    public Map<String,String> login(LoginRequest loginRequest){
        return userService.login(loginRequest);
    }
}
