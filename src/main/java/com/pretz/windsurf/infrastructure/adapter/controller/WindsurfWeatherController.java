package com.pretz.windsurf.infrastructure.adapter.controller;

import com.pretz.windsurf.application.port.WindsurfWeatherPort;
import com.pretz.windsurf.infrastructure.adapter.controller.dto.LocationForecastDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class WindsurfWeatherController {

    private final WindsurfWeatherPort weatherPort;

    public WindsurfWeatherController(WindsurfWeatherPort weatherPort) {
        this.weatherPort = weatherPort;
    }

    //TODO unit tests
    @GetMapping("/")
    public ResponseEntity<LocationForecastDto> getWindsurfingLocation(LocalDate date) {
        return weatherPort.findOptimalWindsurfingLocation(date)
                .map(lf -> new LocationForecastDto(lf.location().name(), lf.windSpeed(), lf.temperature()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }
}
