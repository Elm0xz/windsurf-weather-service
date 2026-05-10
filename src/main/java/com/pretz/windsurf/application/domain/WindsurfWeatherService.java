package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.validation.ForecastDateValidator;
import com.pretz.windsurf.application.domain.service.LocationSelector;
import com.pretz.windsurf.application.port.inbound.WindsurfWeatherPort;
import com.pretz.windsurf.application.port.outbound.LocationsProviderPort;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;

import java.time.LocalDate;
import java.util.Optional;

public class WindsurfWeatherService implements WindsurfWeatherPort {

    private final LocationsProviderPort locationsProvider;
    private final WeatherForecastProviderPort weatherForecastProvider;
    private final LocationSelector locationSelector;
    private final ForecastDateValidator forecastDateValidator;

    public WindsurfWeatherService(LocationsProviderPort locationsProvider,
                                  WeatherForecastProviderPort weatherForecastProvider,
                                  LocationSelector locationSelector,
                                  ForecastDateValidator validator) {
        this.locationsProvider = locationsProvider;
        this.weatherForecastProvider = weatherForecastProvider;
        this.locationSelector = locationSelector;
        this.forecastDateValidator = validator;
    }

    @Override
    public Optional<LocationForecast> findOptimalWindsurfingLocation(LocalDate date) {
        forecastDateValidator.validate(date);

        //TODO logging for found locations
        return locationSelector.selectOptimalLocation(
                weatherForecastProvider.provideForecastsFor(
                        locationsProvider.provideLocations(), date));
    }
}
