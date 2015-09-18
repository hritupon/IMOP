package com.opinion; /**
 * Created by w7 on 5/5/2015.
 */
import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

@SuppressWarnings("unchecked")
public class OpinionApplication extends Application<OpinionConfiguration> {

    public static void main(String[] args) throws Exception{
        new OpinionApplication().run(args);
    }
    @Override
    public void run(OpinionConfiguration opinionConfiguration, Environment environment) throws Exception {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        /*environment.jersey().register(AuthFactory.binder(
                new BasicAuthFactory<>(
                        new OpinionAuthenticator(),
                        "SECURITY REALM",
                        UserDetails.class)));*/

    }

    @Override
    public String getName() {
        return "Opinion";
    }

    @Override
    public void initialize(Bootstrap<OpinionConfiguration> bootstrap) {
        GuiceBundle.Builder<OpinionConfiguration>builder=     GuiceBundle.<OpinionConfiguration>newBuilder();
        builder.addModule(new OpinionModule());
        builder.setConfigClass(OpinionConfiguration.class);
        builder.enableAutoConfig(getClass().getPackage().getName());
        GuiceBundle<OpinionConfiguration> guiceBundle = builder.build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new ConfiguredAssetsBundle("/public"));
    }
}
