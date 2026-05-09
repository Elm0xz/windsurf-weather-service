package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.LocationSelector;
import com.pretz.windsurf.application.port.WindsurfWeatherPort;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.port.LocationsProviderPort;
import com.pretz.windsurf.application.port.WeatherForecastProviderPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WindsurfWeatherService implements WindsurfWeatherPort {

    private final LocationsProviderPort locationsProvider;
    private final WeatherForecastProviderPort weatherForecastProvider;
    private final LocationSelector locationSelector;
    private final ForecastDateValidator forecastDateValidator;

    public WindsurfWeatherService(LocationsProviderPort locationsProvider,
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
