package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.domain.validation.ForecastDateValidator;
import com.pretz.windsurf.application.domain.service.LocationSelector;
import com.pretz.windsurf.application.port.outbound.LocationsProviderPort;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import com.pretz.windsurf.application.port.inbound.WindsurfWeatherPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//TODO refactor later
class WindsurfWeatherServiceTest {

    private WindsurfWeatherPort windsurfWeatherService;

    @Test
    void shouldReturnLocationSelectedFromProvidedLocations() {
        windsurfWeatherService = new WindsurfWeatherService(
                new LocationsProviderMock(),
                new WeatherForecastProviderMock(),
                new LocationSelectorMock(),
                new ForecastDateValidator());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).contains(new LocationForecast(
                new RawLocation("Pcim", "PL"), 15.0, 15.0));
    }

    @Test
    void shouldReturnEmptyWhenNoSuitableLocationFound() {
        windsurfWeatherService = new WindsurfWeatherService(
                new LocationsProviderMock(),
                new WeatherForecastProviderMock(),
                new NoSuitableLocationSelectorMock(),
                new ForecastDateValidator());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).isEmpty();
    }

    static class LocationSelectorMock implements LocationSelector {

        @Override
        public Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts) {
            return Optional.of(new LocationForecast(
                    new RawLocation("Pcim", "PL"), 15.0, 15.0));
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
        public List<Forecast> provideForecastsFor(List<RawLocation> locations, LocalDate date) {
            return List.of();
        }
    }
}
