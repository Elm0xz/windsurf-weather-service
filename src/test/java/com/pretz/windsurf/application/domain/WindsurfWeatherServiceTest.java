package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.Location;
import com.pretz.windsurf.application.domain.port.LocationsProviderPort;
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
        windsurfWeatherService = new BaseWindsurfWeatherService(new LocationsProviderMock(), new LocationSelectorMock());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoSuitableLocationFound() {
        windsurfWeatherService = new BaseWindsurfWeatherService(new LocationsProviderMock(), new NoSuitableLocationSelectorMock());

        var result = windsurfWeatherService.findOptimalWindsurfingLocation(LocalDate.now());

        Assertions.assertThat(result).isEmpty();
    }

    static class LocationSelectorMock implements LocationSelector {

        @Override
        public Optional<Location> selectOptimalLocation(List<Location> locations) {
            return Optional.of(new Location("Pcim", "PL", 15.0, 15.0));
        }
    }

    static class LocationsProviderMock implements LocationsProviderPort {

        @Override
        public List<Location> provideLocations() {
            return List.of();
        }
    }

    static class NoSuitableLocationSelectorMock implements LocationSelector {

        @Override
        public Optional<Location> selectOptimalLocation(List<Location> locations) {
            return Optional.empty();
        }
    }
}
