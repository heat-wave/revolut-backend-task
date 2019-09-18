package dev.heatwave.revolut.exception;

import org.eclipse.jetty.http.HttpStatus;

public class ResourceNotFoundException extends BaseServiceException {

    public ResourceNotFoundException(String message, Object... args) {
        super(HttpStatus.NOT_FOUND_404, message, args);
    }
}
