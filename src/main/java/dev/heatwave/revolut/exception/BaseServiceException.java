package dev.heatwave.revolut.exception;

public class BaseServiceException extends RuntimeException {
    private int statusCode;
    private String message;

    public BaseServiceException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public BaseServiceException(int statusCode, String message, Object... args) {
        this.statusCode = statusCode;
        this.message = String.format(message, args);
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
