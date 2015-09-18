package com.opinion.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
* Created by sutirtha on 5/9/14.
*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedisHost {

    @JsonProperty
    @NotEmpty
    private String host;

    @JsonProperty
    @NotNull
    private int port;

}
