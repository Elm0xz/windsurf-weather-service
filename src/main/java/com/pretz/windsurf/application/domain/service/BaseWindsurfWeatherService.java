package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.LocationSelector;
import com.pretz.windsurf.application.domain.WindsurfWeatherService;
import com.pretz.windsurf.application.domain.model.Location;
import com.pretz.windsurf.application.domain.port.LocationsProviderPort;

import java.time.LocalDate;
import java.util.Optional;

public class BaseWindsurfWeatherService implements WindsurfWeatherService {

    private final LocationsProviderPort locationsProvider;
    private final LocationSelector locationSelector;

    public BaseWindsurfWeatherService(LocationsProviderPort locationsProvider, LocationSelector locationSelector) {
        this.locationsProvider = locationsProvider;
        this.locationSelector = locationSelector;
    }

    @Override
    public Optional<Location> findOptimalWindsurfingLocation(LocalDate date) {
        return locationSelector.selectOptimalLocation(locationsProvider.provideLocations());
    }
}
