package dev.heatwave.revolut.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.heatwave.revolut.model.transfer.Transfer;
import dev.heatwave.revolut.rest.ErrorResponse;
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

        if (result.isPresent()) {
            response.status(HttpStatus.OK_200);
            response.type("application/json");
            return gson.toJson(result.get());
        } else {
            response.status(HttpStatus.NOT_FOUND_404);
            return new ErrorResponse("Transfer with id %d not found", transferId);
        }
    });
}
