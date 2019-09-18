package dev.heatwave.revolut.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.heatwave.revolut.exception.ResourceNotFoundException;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Transfer;
import dev.heatwave.revolut.service.AccountService;
import dev.heatwave.revolut.service.TransferService;
import org.eclipse.jetty.http.HttpStatus;
import spark.Route;
import spark.Service;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountEndpoint implements Endpoint {

    private AccountService accountService;
    private TransferService transferService;

    private final Gson gson = new GsonBuilder().create();

    public AccountEndpoint(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @Override
    public void configure(Service spark, String basePath) {
        post(basePath + "/account", createAccount);
        get(basePath + "/account/:id", getAccount);
        get(basePath + "/account/:id/transfers", getAccountTransfers);
    }

    private final Route createAccount = ((request, response) -> {

        final Account account = gson.fromJson(request.body(), Account.class);
        final Account result = accountService.createAccount(account);
        response.type("application/json");
        return gson.toJson(result);
    });

    private final Route getAccount = ((request, response) -> {

        final long accountId = Long.parseLong(request.params("id"));
        final Optional<Account> result = accountService.getAccountById(accountId);

        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Account with id %d not found", accountId);
        } else {
            response.status(HttpStatus.OK_200);
            response.type("application/json");
            return gson.toJson(result.get());
        }
    });

    private final Route getAccountTransfers = ((request, response) -> {

        final long accountId = Long.parseLong(request.params("id"));
        final List<Transfer> result = transferService.getTransfersByAccountId(accountId);

        response.status(HttpStatus.OK_200);
        response.type("application/json");
        return gson.toJson(result);
    });
}
