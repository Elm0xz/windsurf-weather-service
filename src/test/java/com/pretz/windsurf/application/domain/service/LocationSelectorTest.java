package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.model.Coordinates;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class LocationSelectorTest {

    @Test
    void shouldReturnOptimalLocation() {
        var locationSelector = getLocationSelectorUnderTest();

        var forecasts = List.of(
                forecast("Jastarnia", "PL", 25.2, 10.3),
                forecast("Bridgetown", "BB", 16.3, 33.2),
                forecast("Fortaleza", "BR", 9.9, 35.2),
                forecast("Pissouri", "CY", 17.7, 29.3),
                forecast("Le Morne", "MU", 30.2, 27.3)
        );

        var result = locationSelector.selectOptimalLocation(forecasts);
        Assertions.assertThat(result).contains(new LocationForecast(
                rawLocation("Pissouri", "CY"), 17.7, 29.3));
    }

    @Test
    void shouldReturnNoLocationIfNoneMeetsCriteria() {
        var locationSelector = getLocationSelectorUnderTest();

        var forecasts = List.of(
                forecast("Jastarnia", "PL", 25.2, 10.3),
                forecast("Bridgetown", "BB", 3.3, 23.5),
                forecast("Fortaleza", "BR", 9.9, 35.2)
        );

        var result = locationSelector.selectOptimalLocation(forecasts);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnNoLocationIfNoLocationPassed() {
        var locationSelector = getLocationSelectorUnderTest();

        List<Forecast> forecasts = List.of();

        var result = locationSelector.selectOptimalLocation(forecasts);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnFirstOptimalLocationInCaseMoreThanOneIsOptimal() {
        var locationSelector = getLocationSelectorUnderTest();

        var forecasts = List.of(
                forecast("Jastarnia", "PL", 15.0, 25.0),
                forecast("Bridgetown", "BB", 15.0, 25.0),
                forecast("Fortaleza", "BR", 9.9, 35.2)
        );

        var result = locationSelector.selectOptimalLocation(forecasts);
        Assertions.assertThat(result).contains(new LocationForecast(
                rawLocation("Jastarnia", "PL"), 15.0, 25.0));
    }

    @Test
    void shouldRejectNullLocations() {
        var locationSelector = getLocationSelectorUnderTest();

        Assertions.assertThatNullPointerException()
                .isThrownBy(() -> locationSelector.selectOptimalLocation(null));
    }

    private LocationSelector getLocationSelectorUnderTest() {
        return new BaseLocationSelector();
    }

    private Forecast forecast(String name, String country, double windSpeed, double temperature) {
        return new Forecast(
                rawLocation(name, country),
                LocalDate.now(),
                windSpeed,
                temperature
        );
    }

    private RawLocation rawLocation(String name, String country) {
        return new RawLocation(name, country, coordinatesFor(name));
    }

    private Coordinates coordinatesFor(String name) {
        return switch (name) {
            case "Jastarnia" -> new Coordinates(54.6961, 18.6787);
            case "Bridgetown" -> new Coordinates(13.0975, -59.6167);
            case "Fortaleza" -> new Coordinates(-3.7319, -38.5267);
            case "Pissouri" -> new Coordinates(34.6694, 32.7019);
            case "Le Morne" -> new Coordinates(-20.4561, 57.3139);
            default -> new Coordinates(0.0, 0.0);
        };
    }
}
