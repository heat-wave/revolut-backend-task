package dev.heatwave.revolut.rest.endpoint;

import spark.Service;

public interface Endpoint {
    void configure(Service spark, String basePath);
}
