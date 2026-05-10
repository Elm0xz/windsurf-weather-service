package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.port.inbound.WindsurfWeatherPort;
import com.pretz.windsurf.infrastructure.adapter.inbound.api.dto.LocationForecastDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class WindsurfWeatherController implements WindsurfWeatherApi {

    private final WindsurfWeatherPort weatherPort;

    public WindsurfWeatherController(WindsurfWeatherPort weatherPort) {
        this.weatherPort = weatherPort;
    }

    @Override
    public ResponseEntity<LocationForecastDto> getWindsurfingLocation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return weatherPort.findOptimalWindsurfingLocation(date)
                .map(lf -> new LocationForecastDto(lf.location().name(), lf.windSpeed(), lf.temperature()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }
}
