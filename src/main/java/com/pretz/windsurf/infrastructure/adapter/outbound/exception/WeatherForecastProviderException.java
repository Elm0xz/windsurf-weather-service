package com.pretz.windsurf.infrastructure.adapter.outbound.exception;

public class WeatherForecastProviderException extends RuntimeException {
    public WeatherForecastProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
