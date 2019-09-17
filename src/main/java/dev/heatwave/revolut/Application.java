package dev.heatwave.revolut;

import dev.heatwave.revolut.persistence.PersistenceManager;
import dev.heatwave.revolut.rest.endpoint.AccountEndpoint;
import dev.heatwave.revolut.rest.RestContext;
import dev.heatwave.revolut.service.AccountServiceImpl;

public class Application {

    public static void main(String[] args) {

//        new Application().run();

//        post("/account");
//        post("/account/:id/state");
//        get("/account/:id");
//        get("/account/:id/transactions");


        RestContext context = new RestContext(8080, "/api");

        context.addEndpoint(new AccountEndpoint(new AccountServiceImpl(PersistenceManager.getEntityManagerFactory())));
    }
}
