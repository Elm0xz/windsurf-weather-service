package com.pretz.windsurf.infrastructure.adapter;

public class WeatherApiClientException extends RuntimeException {

    public WeatherApiClientException(String message) {
        super(message);
    }

    public WeatherApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
