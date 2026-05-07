package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.domain.port.LocationsProviderPort;
import com.pretz.windsurf.application.domain.port.WeatherForecastProviderPort;
import com.pretz.windsurf.application.domain.service.BaseWindsurfWeatherService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class WindsurfWeatherServiceTest {

    private WindsurfWeatherService windsurfWeatherService;

    @Test
    void shouldReturnLocationSelectedFromProvidedLocations() {
        windsurfWeatherService = new BaseWindsurfWeatherService(
                new LocationsProviderMock(),
                new WeatherForecastProviderMock(),
                new LocationSelectorMock());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoSuitableLocationFound() {
        windsurfWeatherService = new BaseWindsurfWeatherService(
                new LocationsProviderMock(),
                new WeatherForecastProviderMock(),
                new NoSuitableLocationSelectorMock());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).isEmpty();
    }

    //TODO unit test for null date? (or for validators)

    static class LocationSelectorMock implements LocationSelector {

        @Override
        public Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts) {
            return Optional.of(new LocationForecast(new RawLocation("Pcim", "PL"), 15.0, 15.0));
        }
    }

    static class LocationsProviderMock implements LocationsProviderPort {

        @Override
        public List<RawLocation> provideLocations() {
            return List.of();
        }
    }

    static class NoSuitableLocationSelectorMock implements LocationSelector {

        @Override
        public Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts) {
            return Optional.empty();
        }
    }

    static class WeatherForecastProviderMock implements WeatherForecastProviderPort {

        @Override
        public List<Forecast> getForecastsFor(List<RawLocation> locations, LocalDate date) {
            return List.of();
        }
    }
}
