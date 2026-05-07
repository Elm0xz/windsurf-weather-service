package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.LocationSelector;
import com.pretz.windsurf.application.domain.WindsurfWeatherService;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.port.LocationsProviderPort;
import com.pretz.windsurf.application.domain.port.WeatherForecastProviderPort;

import java.time.LocalDate;
import java.util.Optional;

public class BaseWindsurfWeatherService implements WindsurfWeatherService {

    private final LocationsProviderPort locationsProvider;
    private final WeatherForecastProviderPort weatherForecastProvider;
    private final LocationSelector locationSelector;
    private final ForecastDateValidator forecastDateValidator;

    public BaseWindsurfWeatherService(LocationsProviderPort locationsProvider,
                                      WeatherForecastProviderPort weatherForecastProvider,
                                      LocationSelector locationSelector) {
        this.locationsProvider = locationsProvider;
        this.weatherForecastProvider = weatherForecastProvider;
        this.locationSelector = locationSelector;
        forecastDateValidator = new ForecastDateValidator();
    }

    @Override
    public Optional<LocationForecast> findOptimalWindsurfingLocation(LocalDate date) {
        forecastDateValidator.validate(date);

        return locationSelector.selectOptimalLocation(
                weatherForecastProvider.getForecastsFor(
                        locationsProvider.provideLocations(), date));
    }
}
