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
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSnakeCase
public class LoginRequest {


    @NotNull
    @JsonProperty
    String userName;

    @NotNull
    @JsonProperty
    String password;

    public LoginRequest(){

    }
}
