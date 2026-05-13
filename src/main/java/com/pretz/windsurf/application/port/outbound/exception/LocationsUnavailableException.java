package com.pretz.windsurf.application.port.outbound.exception;

public class LocationsUnavailableException extends RuntimeException {

    public LocationsUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
