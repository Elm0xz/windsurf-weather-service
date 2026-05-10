package com.pretz.windsurf.infrastructure.adapter.outbound.exception;

public class WeatherApiClientException extends RuntimeException {

    public WeatherApiClientException(String message) {
        super(message);
    }

    public WeatherApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
