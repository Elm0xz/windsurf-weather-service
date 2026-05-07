package com.pretz.windsurf.application.domain.service;

public class InvalidForecastDateException extends RuntimeException {

    public InvalidForecastDateException(String message) {
        super(message);
    }
}
