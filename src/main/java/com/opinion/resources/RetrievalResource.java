package com.opinion.resources;

import com.google.inject.Inject;
import com.opinion.models.Post;
import com.opinion.models.PostGetResponse;
import com.opinion.models.TopicResponse;
import com.opinion.services.IngestionService;
import com.opinion.services.RetrievalService;
import com.opinion.services.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by hritupon on 10/5/15.
 */
@Path("/retrieve")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RetrievalResource {

    private UserService userService;
    private RetrievalService retrievalService;
    @Inject
    public RetrievalResource(UserService userService,RetrievalService retrievalService){
        this.userService=userService;
        this.retrievalService=retrievalService;
    }

    @GET
    @Path("/get/{user}/{id}")
    public PostGetResponse get(@PathParam("id") long postId,@PathParam("user") String userId)throws Exception{
        //retrieve
        userService.checkValidUser(userId);
        return new PostGetResponse(userId,postId,"SomeData");
    }

    @GET
    @Path("/get")
    public PostGetResponse getAllPosts(@QueryParam("user") String userId,@QueryParam("data") String data)throws Exception{
        //retrieve
        userService.checkValidUser(userId);
        return new PostGetResponse(userId,1234,data);
    }

    @GET
    @Path("/topic")
    public Map<String,List<TopicResponse>> getTopics() throws Exception{
        return retrievalService.getTopicTitles();
    }

    @GET
    @Path("/topic/{id}")
    public Map<String, List<String>> getTopicDetails(@PathParam("id") String id) throws Exception{
        return retrievalService.getTopicDetails(id);
    }

    @GET
    @Path("/topic/posts/{id}")
    public Map<String, List<Post>> getTopicPosts(@PathParam("id") String id) throws Exception{
        return retrievalService.getTopicPosts(id);
    }
}
