package dev.heatwave.revolut;

import dev.heatwave.revolut.persistence.PersistenceManager;
import dev.heatwave.revolut.rest.RestContext;
import dev.heatwave.revolut.rest.endpoint.AccountEndpoint;
import dev.heatwave.revolut.rest.endpoint.TransferEndpoint;
import dev.heatwave.revolut.service.AccountServiceImpl;
import dev.heatwave.revolut.service.TransferServiceImpl;

import javax.persistence.EntityManagerFactory;

public class Application {

    public static void main(String[] args) {

        RestContext context = new RestContext(8080, "/api");

        EntityManagerFactory entityManagerFactory = PersistenceManager.getEntityManagerFactory();

        context.addEndpoint(new AccountEndpoint(
                        new AccountServiceImpl(entityManagerFactory),
                        new TransferServiceImpl(new AccountServiceImpl(entityManagerFactory), entityManagerFactory)
                )
        );

        context.addEndpoint(new TransferEndpoint(
                        new TransferServiceImpl(
                                new AccountServiceImpl(entityManagerFactory), entityManagerFactory)
                )
        );
    }
}
