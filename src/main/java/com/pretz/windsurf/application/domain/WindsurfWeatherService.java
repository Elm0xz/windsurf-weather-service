package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.Location;

import java.time.LocalDate;
import java.util.Optional;

public interface WindsurfWeatherService {

    Optional<Location> findOptimalWindsurfingLocation(LocalDate date);
}
