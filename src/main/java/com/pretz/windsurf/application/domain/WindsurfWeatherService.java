package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.LocationForecast;

import java.time.LocalDate;
import java.util.Optional;

public interface WindsurfWeatherService {

    Optional<LocationForecast> findOptimalWindsurfingLocation(LocalDate date);
}
