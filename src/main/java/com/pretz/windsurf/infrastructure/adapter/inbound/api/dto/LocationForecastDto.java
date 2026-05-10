package com.pretz.windsurf.infrastructure.adapter.inbound.api.dto;

public record LocationForecastDto(String location, double windSpeed, double temperature) {
}
