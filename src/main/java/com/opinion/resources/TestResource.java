package com.opinion.resources;

import com.opinion.models.TestResponse;
import com.opinion.models.UserDetails;
import io.dropwizard.auth.Auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hritupon on 9/18/2015.
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource {

    @GET
    @Path("/ping")
    public TestResponse test(@Auth UserDetails userDetails)throws Exception{
        TestResponse tr=new TestResponse();
        tr.setTestString("pong");
        return tr;
    }
    @POST
    @Path("/post")
    public TestResponse postTest(@Auth UserDetails userDetails)throws Exception{
        TestResponse tr=new TestResponse();
        tr.setTestString("pong");
        return tr;
    }
}
