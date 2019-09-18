package dev.heatwave.revolut.rest;

import dev.heatwave.revolut.exception.BaseServiceException;
import dev.heatwave.revolut.rest.endpoint.Endpoint;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import static spark.Spark.exception;
import static spark.Spark.port;

public class RestContext {

    private static final Logger logger = LoggerFactory.getLogger(RestContext.class);

    private final Service spark;

    private final String basePath;

    public RestContext(int port, String basePath) {
        this.basePath = basePath;
        this.spark = Service.ignite();
        port(port);

        exception(BaseServiceException.class, (exception, request, response) -> {
            response.status(exception.getStatusCode());
            response.body(exception.getMessage());
        });
        exception(Exception.class, (exception, request, response) -> {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.body(exception.getMessage());
        });
    }

    public void addEndpoint(Endpoint endpoint) {
        endpoint.configure(spark, basePath);
        logger.debug("REST endpoint registered for {}.", endpoint.getClass().getSimpleName());
    }
}
