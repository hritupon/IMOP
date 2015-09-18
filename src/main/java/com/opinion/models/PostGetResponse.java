package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by w7 on 5/5/2015.
 */
@AllArgsConstructor
@Getter @Setter
@JsonSnakeCase
public class PostGetResponse {
    @JsonProperty
    @NotNull
    String userId;

    @JsonProperty
    @NotNull
    long postId;

    @JsonProperty
    String data;
}
