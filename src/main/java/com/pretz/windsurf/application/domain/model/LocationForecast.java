package com.pretz.windsurf.application.domain.model;

/**
 * Entity representing location with additional info about weather conditions.
 * @param location
 * @param windSpeed
 * @param temperature
 */
public record LocationForecast(RawLocation location, double windSpeed, double temperature) {
}
