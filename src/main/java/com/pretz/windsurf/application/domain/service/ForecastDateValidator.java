package com.pretz.windsurf.application.domain.service;

import java.time.LocalDate;

public class ForecastDateValidator {

    private static final String NULL_MESSAGE = "Forecast date must not be null";
    private static final String FORECAST_DATE_ERROR_MESSAGE = "Forecast date should be in 7 day forecast range";

    public void validate(LocalDate date) {
        if (date == null) {
            throw new InvalidForecastDateException(NULL_MESSAGE);
        }

        if (date.isAfter(LocalDate.now().plusDays(7)) || date.isBefore(LocalDate.now())) {
            throw new InvalidForecastDateException(FORECAST_DATE_ERROR_MESSAGE);
        }
    }
}
