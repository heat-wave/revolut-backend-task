package dev.heatwave.revolut;

import dev.heatwave.revolut.rest.RestContext;
import dev.heatwave.revolut.rest.endpoint.AccountEndpoint;
import dev.heatwave.revolut.rest.endpoint.TransferEndpoint;
import dev.heatwave.revolut.service.AccountServiceImpl;
import dev.heatwave.revolut.service.TransferServiceImpl;
import dev.heatwave.revolut.util.PropertyResolver;

public class Application {

    public static void main(String[] args) {

        RestContext context = new RestContext(PropertyResolver.getConfiguration().getInt(PropertyResolver.SERVER_PORT), "/api");

        context.addEndpoint(new AccountEndpoint(
                        new AccountServiceImpl(),
                        new TransferServiceImpl(new AccountServiceImpl())
                )
        );
        context.addEndpoint(new TransferEndpoint(
                        new TransferServiceImpl(new AccountServiceImpl())
                )
        );
    }
}
