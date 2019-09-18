package dev.heatwave.revolut.rest;

import dev.heatwave.revolut.rest.endpoint.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import static spark.Spark.port;

public class RestContext {

    private static final Logger logger = LoggerFactory.getLogger(RestContext.class);

    private final Service spark;

    private final String basePath;

    public RestContext(int port, String basePath) {
        this.basePath = basePath;
        this.spark = Service.ignite();
        port(port);
    }

    public void addEndpoint(Endpoint endpoint) {
        endpoint.configure(spark, basePath);
        logger.debug("REST endpoint registered for {}.", endpoint.getClass().getSimpleName());
    }
}
