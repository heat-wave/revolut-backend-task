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

    private RestContext() {}

    private static final Logger logger = LoggerFactory.getLogger(RestContext.class);
    private static final String basePath = "/api";

    private static class SingletonHolder {
        private static final Service INSTANCE = Service.ignite();
    }

    private static Service getInstance() {
        return RestContext.SingletonHolder.INSTANCE;
    }

    public static void init(int listenPort) {
        setupExceptionHandling();
        try {
            port(listenPort);
        } catch (IllegalStateException exception) {
            logger.warn("Port mapping failed", exception);
        }
    }

    private static void setupExceptionHandling() {
        exception(BaseServiceException.class, (exception, request, response) -> {
            response.status(exception.getStatusCode());
            response.body(exception.getMessage());
        });
        exception(Exception.class, (exception, request, response) -> {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.body(exception.getMessage());
        });
    }

    public static void addEndpoint(Endpoint endpoint) {
        endpoint.configure(getInstance(), basePath);
        logger.debug("REST endpoint registered for {}.", endpoint.getClass().getSimpleName());
    }
}
