package dev.heatwave.revolut.exception;

import org.eclipse.jetty.http.HttpStatus;

public class ForbiddenOperationException extends BaseServiceException {

    public ForbiddenOperationException(String message, Object... args) {
        super(HttpStatus.BAD_REQUEST_400, message, args);
    }
}
