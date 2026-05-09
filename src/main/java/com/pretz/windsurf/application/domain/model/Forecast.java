package com.pretz.windsurf.application.domain.model;

import java.time.LocalDate;

/**
 * Entity representing full weather forecast for a location and particular day.
 * @param location
 * @param requestDate
 * @param forecastDate
 * @param windSpeed
 * @param temperature
 */
public record Forecast(RawLocation location, LocalDate requestDate, LocalDate forecastDate,
                       double windSpeed, double temperature) {

    public double windsurfingValue() {
        return windSpeed() * 3 + temperature();
    }
}
