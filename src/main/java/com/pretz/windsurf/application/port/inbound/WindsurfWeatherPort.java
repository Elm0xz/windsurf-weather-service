package com.pretz.windsurf.application.port.inbound;

import com.pretz.windsurf.application.domain.model.LocationForecast;

import java.time.LocalDate;
import java.util.Optional;

public interface WindsurfWeatherPort {

    Optional<LocationForecast> findOptimalWindsurfingLocation(LocalDate date);
}
