package dev.heatwave.revolut.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.heatwave.revolut.model.account.Account;
import dev.heatwave.revolut.rest.ErrorResponse;
import dev.heatwave.revolut.service.AccountService;
import org.eclipse.jetty.http.HttpStatus;
import spark.Route;
import spark.Service;

import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountEndpoint implements Endpoint {

    private AccountService accountService;

    private final Gson gson = new GsonBuilder().create();

    public AccountEndpoint(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void configure(Service spark, String basePath) {
        post(basePath + "/account", createAccount);
        get(basePath + "/account/:id", getAccount);
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

        if (result.isPresent()) {
            response.status(HttpStatus.OK_200);
            response.type("application/json");
            return gson.toJson(result.get());
        } else {
            response.status(HttpStatus.NOT_FOUND_404);
            return new ErrorResponse("Account with id %d not found", accountId);
        }
    });
}
