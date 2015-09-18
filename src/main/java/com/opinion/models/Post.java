package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by w7 on 5/5/2015.
 */
@Getter @Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSnakeCase
public class Post {

    @NotNull
    @JsonProperty
    String postData;

    @JsonProperty
    String userName;

    @JsonProperty
    String auth;

    @JsonProperty
    String topicId;

    @JsonIgnoreProperties
    String postId="opinion:"+UUID.randomUUID().toString();

    public Post(){

    }
    public HashMap<String,String> getPostParamsData(){
        HashMap map= Maps.newHashMap();
        map.put("user_name",userName);
        map.put("data",postData);
        java.util.Date date= new java.util.Date();
        Timestamp timestamp= new Timestamp(date.getTime());
        map.put("time",timestamp.getTime());
        return map;
    }
}
