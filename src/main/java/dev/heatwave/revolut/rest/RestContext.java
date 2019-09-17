package dev.heatwave.revolut.rest;

import dev.heatwave.revolut.rest.endpoint.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

public class RestContext {

    private static final Logger logger = LoggerFactory.getLogger(RestContext.class);

    private final Service spark;

    private final String basePath;

    public RestContext(int port, String basePath) {
        this.basePath = basePath;
        spark = Service.ignite().port(port);
    }

    public void addEndpoint(Endpoint endpoint) {
        endpoint.configure(spark, basePath);
        logger.info("REST endpoints registered for {}.", endpoint.getClass().getSimpleName());
    }
}
