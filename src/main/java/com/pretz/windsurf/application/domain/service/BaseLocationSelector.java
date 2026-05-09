package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.LocationSelector;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.LocationForecast;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BaseLocationSelector implements LocationSelector {

    @Override
    public Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts) {
        //TODO log warn instead
        Objects.requireNonNull(forecasts, "forecasts must not be null");

        return forecasts.stream()
                .filter(this::isWindInRange)
                .filter(this::isTemperatureInRange)
                .max(this::compareValues)
                .map(fc -> new LocationForecast(fc.location(),
                        fc.windSpeed(), fc.temperature()));
    }

    private boolean isWindInRange(Forecast loc) {
        return loc.windSpeed() >= 5.0 && loc.windSpeed() <= 18.0;
    }

    private boolean isTemperatureInRange(Forecast loc) {
        return loc.temperature() >= 5.0 && loc.temperature() <= 35.0;
    }

    private int compareValues(Forecast loc1, Forecast loc2) {
        return Double.compare(loc1.windsurfingValue(), loc2.windsurfingValue());
    }

}
