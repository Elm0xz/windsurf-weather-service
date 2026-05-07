package com.pretz.windsurf.application.domain.model;

import java.time.LocalDate;

/**
 * Entity representing a request for weather forecast in a particular location.
 * @param location
 * @param requestDate
 * @param forecastDate
 */
public record ForecastRequest(RawLocation location, LocalDate requestDate, LocalDate forecastDate) {

}
