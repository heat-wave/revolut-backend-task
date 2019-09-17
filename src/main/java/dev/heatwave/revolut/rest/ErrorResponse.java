package dev.heatwave.revolut.rest;

public class ErrorResponse {

    private String message;

    public ErrorResponse(String message, Object... args) {
        this.message = String.format(message, args);
    }

    public ErrorResponse(Exception exception) {
        this.message = exception.getMessage();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
