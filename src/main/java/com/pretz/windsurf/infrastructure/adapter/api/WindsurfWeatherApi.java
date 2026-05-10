package com.pretz.windsurf.infrastructure.adapter.api;

import com.pretz.windsurf.infrastructure.adapter.api.dto.LocationForecastDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public interface WindsurfWeatherApi {

    @GetMapping("/api/windsurfing-location")
    ResponseEntity<LocationForecastDto> getWindsurfingLocation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
