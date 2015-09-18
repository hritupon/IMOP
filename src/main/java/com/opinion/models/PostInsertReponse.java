package com.opinion.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by w7 on 5/5/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@JsonSnakeCase
public class PostInsertReponse {
    @JsonProperty
    String responseCode;
}
