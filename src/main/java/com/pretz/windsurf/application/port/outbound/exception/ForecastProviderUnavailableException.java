package com.pretz.windsurf.application.port.outbound.exception;

public class ForecastProviderUnavailableException extends RuntimeException {

    public ForecastProviderUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
