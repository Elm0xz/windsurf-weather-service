package com.pretz.windsurf.application.domain.model;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Entity representing full weather forecast for a location and particular day.
 *
 * @param location
 * @param forecastDate
 * @param windSpeed
 * @param temperature
 */
public record Forecast(RawLocation location, LocalDate forecastDate,
                       double windSpeed, double temperature) {

    public static Comparator<Forecast> byWindsurfingValue() {
        return Comparator.comparingDouble(Forecast::windsurfingValue);
    }

    public boolean isWindInRange() {
        return windSpeed() >= 5.0 && windSpeed() <= 18.0;
    }

    public boolean isTemperatureInRange() {
        return temperature() >= 5.0 && temperature() <= 35.0;
    }

    private double windsurfingValue() {
        return windSpeed() * 3 + temperature();
    }
}
