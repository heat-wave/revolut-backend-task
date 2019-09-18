package dev.heatwave.revolut.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.heatwave.revolut.exception.ResourceNotFoundException;
import dev.heatwave.revolut.model.Transfer;
import dev.heatwave.revolut.service.TransferService;
import org.eclipse.jetty.http.HttpStatus;
import spark.Route;
import spark.Service;

import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferEndpoint implements Endpoint {

    private TransferService transferService;

    private final Gson gson = new GsonBuilder().create();

    public TransferEndpoint(TransferService transferService) {
        this.transferService = transferService;
    }


    @Override
    public void configure(Service spark, String basePath) {
        post(basePath + "/transfer", createTransfer);
        get(basePath + "/transfer/:id", getTransfer);
    }

    private final Route createTransfer = ((request, response) -> {

        final Transfer transfer = gson.fromJson(request.body(), Transfer.class);
        final Transfer result = transferService.createTransfer(transfer);
        response.type("application/json");
        return gson.toJson(result);
    });

    private final Route getTransfer = ((request, response) -> {

        final long transferId = Long.parseLong(request.params("id"));
        final Optional<Transfer> result = transferService.getTransferById(transferId);

        if (!result.isPresent()) {
            throw new ResourceNotFoundException("Transfer with id %d not found", transferId);
        } else {
            response.status(HttpStatus.OK_200);
            response.type("application/json");
            return gson.toJson(result.get());
        }
    });
}
