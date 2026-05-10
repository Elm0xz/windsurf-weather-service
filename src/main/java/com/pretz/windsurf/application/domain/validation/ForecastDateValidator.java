package com.pretz.windsurf.application.domain.validation;

import java.time.LocalDate;

public class ForecastDateValidator {

    private static final String NULL_MESSAGE = "Forecast date must not be null";
    private static final String FORECAST_DATE_ERROR_MESSAGE = "Forecast date should be between today and 7 days from today";

    public void validate(LocalDate date) {
        if (date == null) {
            throw new InvalidForecastDateException(NULL_MESSAGE);
        }

        LocalDate today = LocalDate.now();

        if (date.isAfter(today.plusDays(7)) || date.isBefore(today)) {
            throw new InvalidForecastDateException(FORECAST_DATE_ERROR_MESSAGE);
        }
    }
}
