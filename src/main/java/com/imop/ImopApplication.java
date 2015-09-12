package com.imop;

import com.imop.com.imop.com.impo.health.TemplateHealthCheck;
import com.imop.com.imop.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ImopApplication extends Application<ImopConfiguration> {

    public static void main(String[] args) throws Exception {
        new ImopApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<ImopConfiguration> bootstrap) {
    }

    @Override
    public void run(ImopConfiguration configuration, Environment environment) throws Exception {

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        environment.jersey().register(resource);


        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}