package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by hritupon on 23/5/15.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSnakeCase
@AllArgsConstructor
public class UserAddResponse {
        @JsonProperty
        String userName;

        @JsonProperty
        String id;

        @JsonProperty
        String statusCode;

    public UserAddResponse(){

    }
}
