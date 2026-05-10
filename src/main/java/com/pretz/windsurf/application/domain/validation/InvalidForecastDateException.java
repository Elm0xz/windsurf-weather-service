package com.pretz.windsurf.application.domain.validation;

public class InvalidForecastDateException extends RuntimeException {

    public InvalidForecastDateException(String message) {
        super(message);
    }
}
