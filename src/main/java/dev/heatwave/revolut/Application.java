package dev.heatwave.revolut;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.heatwave.revolut.configuration.GuiceConfiguration;
import dev.heatwave.revolut.rest.RestContext;
import dev.heatwave.revolut.rest.endpoint.AccountEndpoint;
import dev.heatwave.revolut.rest.endpoint.TransferEndpoint;
import dev.heatwave.revolut.util.PropertyResolver;

public class Application {

    public static void main(String[] args) {

        RestContext context = new RestContext(PropertyResolver.getConfiguration().getInt(PropertyResolver.SERVER_PORT), "/api");
        Injector injector = Guice.createInjector(new GuiceConfiguration());

        context.addEndpoint(injector.getInstance(AccountEndpoint.class));
        context.addEndpoint(injector.getInstance(TransferEndpoint.class));
    }
}
