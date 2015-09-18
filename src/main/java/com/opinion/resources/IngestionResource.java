package com.opinion.resources;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.opinion.models.*;
import com.opinion.services.IngestionService;
import com.opinion.services.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by w7 on 5/5/2015.
 */
@Path("/ingest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IngestionResource {

    private UserService userService;
    private IngestionService ingestionService;
    @Inject
    public IngestionResource(UserService userService,IngestionService ingestionService) {
        this.userService=userService;
        this.ingestionService=ingestionService;
    }
    @POST
    @Path("/comment")
    public PostInsertReponse insert(Post Post)throws Exception{
        //insert
        return new PostInsertReponse("200");
    }

    @POST
    @Path("/topics")
    public PostInsertReponse insert(List<TopicRequest> topicRequest)throws Exception{
        //insert
        ingestionService.insertTopic(topicRequest);
        return new PostInsertReponse("200");
    }

    @POST
    @Path("/topic")
    public PostInsertReponse insert(TopicRequest topicRequest)throws Exception{
        //insert
        List<TopicRequest> topicRequests= Lists.newArrayList();
        topicRequests.add(topicRequest);
        ingestionService.insertTopic(topicRequests);
        return new PostInsertReponse("200");
    }



    @POST
    @Path("/opinion")
    public boolean InsertOpinion(Post post) throws Exception{
        ingestionService.insertOpinion(post);
        return true;
    }

}
