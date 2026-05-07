package com.pretz.windsurf.application.domain.service;

import java.time.LocalDate;

public class ForecastDateValidator {

    public void validate(LocalDate date) {
        if (date == null) {
            throw new InvalidForecastDateException("Forecast date must not be null");
        }

        if (date.isAfter(LocalDate.now().plusDays(7))) {
            throw new InvalidForecastDateException("Forecast date must not be later than 7 days after today");
        }
    }
}
