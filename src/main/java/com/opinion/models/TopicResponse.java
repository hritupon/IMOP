package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by hritupon on 16/5/15.
 */
@Getter @Setter
@AllArgsConstructor
@JsonSnakeCase
public class TopicResponse {
    @JsonProperty
    String topicName;

    @JsonProperty
    String topicId;

    @JsonProperty
    int responseCount;

    public TopicResponse(){

    }
}
