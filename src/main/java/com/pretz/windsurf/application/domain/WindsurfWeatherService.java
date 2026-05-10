package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.service.LocationSelector;
import com.pretz.windsurf.application.domain.validation.ForecastDateValidator;
import com.pretz.windsurf.application.port.inbound.WindsurfWeatherPort;
import com.pretz.windsurf.application.port.outbound.LocationsProviderPort;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;

public class WindsurfWeatherService implements WindsurfWeatherPort {

    private static final Logger log = LoggerFactory.getLogger(WindsurfWeatherService.class);

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

        Optional<LocationForecast> result = locationSelector.selectOptimalLocation(
                weatherForecastProvider.provideForecastsFor(
                        locationsProvider.provideLocations(), date));

        result.ifPresentOrElse(
                locationForecast -> logConditions(date, locationForecast),
                () -> log.info("No optimal conditions found for date {}", date)
        );

        return result;
    }

    private static void logConditions(LocalDate date, LocationForecast locationForecast) {
        log.info(
                "Found optimal conditions for date {} in {}, {} at coordinates [{}, {}]: wind speed {}, temperature {}",
                date,
                locationForecast.location().name(),
                locationForecast.location().countryCode(),
                locationForecast.location().coordinates().latitude(),
                locationForecast.location().coordinates().longitude(),
                locationForecast.windSpeed(),
                locationForecast.temperature()
        );
    }
}
