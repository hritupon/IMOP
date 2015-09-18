package com.opinion.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ramachandra.as
 * Date: 23/11/14
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartitionedRedis {

    @JsonProperty
    @Valid
    private RedisHost master;

    @JsonProperty
    @Valid
    private List<RedisHost> slaveHosts;

}
