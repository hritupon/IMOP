package com.imop;

import  io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;
import javax.validation.Valid;


public class ImopConfiguration extends Configuration {
    @Getter
    @Setter
    @Valid
    String template;

    private final String defaultName="IMOP";

    public String getDefaultName(){
        return defaultName;
    }
}
