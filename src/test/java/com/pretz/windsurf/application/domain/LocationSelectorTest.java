package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.domain.service.BaseLocationSelector;
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
                new RawLocation("Pissouri", "CY"), 17.7, 29.3));
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
                new RawLocation("Jastarnia", "PL"), 15.0, 25.0));
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
                LocalDate.now(),
                windSpeed,
                temperature
        );
    }

    private RawLocation rawLocation(String name, String country) {
        return new RawLocation(name, country);
    }
}
