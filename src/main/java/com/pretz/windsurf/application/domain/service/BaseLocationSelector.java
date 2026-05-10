package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BaseLocationSelector implements LocationSelector {

    @Override
    public Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts) {
        //TODO log warn instead
        Objects.requireNonNull(forecasts, "forecasts must not be null");

        return forecasts.stream()
                .filter(Forecast::isWindInRange)
                .filter(Forecast::isTemperatureInRange)
                .max(Forecast.byWindsurfingValue())
                .map(fc -> new LocationForecast(fc.location(),
                        fc.windSpeed(), fc.temperature()));
    }
}
