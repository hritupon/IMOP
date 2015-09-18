package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hritupon on 10/5/15.
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSnakeCase
public class TopicRequest {
    @JsonProperty @NotNull
    String topic;

    @JsonProperty @NotNull
    String category;

    @JsonProperty @NotNull
    String userName;

    @JsonProperty @NotNull
    String auth;

    @JsonProperty @NotNull
    String needPoll;

    @JsonProperty @NotNull
    String permanent;

    @JsonIgnoreProperties
    String id="topic:"+UUID.randomUUID().toString();

    public String getRawKey(){
        return "TOPIC";
    }
    public String getTopicCategory(){
        return category;
    }
    public String getTopicId(){
        return id;
    }
    public String getTopic(){
        return topic;
    }
    public Map<String,String> getTopicData(){
        Map<String,String> map= Maps.newHashMap();
        map.put("category",this.category);
        map.put("data",this.topic);
        map.put("need_poll",this.needPoll);
        map.put("permanent",this.permanent);
        return map;
    }
}
