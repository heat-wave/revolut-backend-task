package dev.heatwave.revolut.rest.endpoint;

import dev.heatwave.revolut.service.TransactionService;
import spark.Service;

public class TransactionEndpoint implements Endpoint {

    private final TransactionService transactionService;

    public TransactionEndpoint(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @Override
    public void configure(Service spark, String basePath) {

    }
}
