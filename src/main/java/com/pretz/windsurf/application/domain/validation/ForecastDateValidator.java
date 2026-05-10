package com.pretz.windsurf.application.domain.validation;

import java.time.LocalDate;

public class ForecastDateValidator {

    private static final String NULL_MESSAGE = "Forecast date must not be null";
    private static final String FORECAST_DATE_ERROR_MESSAGE = "Forecast date should be in the 7 forecast day range";

    public void validate(LocalDate date) {
        if (date == null) {
            throw new InvalidForecastDateException(NULL_MESSAGE);
        }

        LocalDate today = LocalDate.now();

        if (date.isAfter(today.plusDays(6)) || date.isBefore(today)) {
            throw new InvalidForecastDateException(FORECAST_DATE_ERROR_MESSAGE);
        }
    }
}
