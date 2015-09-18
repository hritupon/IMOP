package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by hritupon on 23/5/15.
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSnakeCase
@AllArgsConstructor
public class AddFollowerRequest {

    //"userId" will be following the opinions of "follower Id"

    @JsonProperty
    @NotNull
    String followerId;

    @JsonProperty
    @NotNull
    String userId;

    public AddFollowerRequest() {

    }
}