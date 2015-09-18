package com.opinion.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opinion.models.enums.PartitionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Created by sutirtha on 04/09/14.
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor @NoArgsConstructor
public class RedisConfiguration {

    @JsonProperty
    @Valid
    private RedisHost config;

    @JsonProperty
    @Valid
    private Map<PartitionType, PartitionedRedis> partitions;

    @Min(2)
    @Max(4000)
    @JsonProperty
    private int timeout = 2;

    @Min(0)
    @Max(15)
    @JsonProperty
    private int db = 0;

    @Min(8)
    @Max(4096)
    @JsonProperty
    private int masterMaxThreads;

    @Min(8)
    @Max(4096)
    @JsonProperty
    private int slaveMaxThreads;

}
