package com.pretz.windsurf.infrastructure.adapter;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;

import java.time.LocalDate;
import java.util.List;

public interface WeatherApiClient {

    List<Forecast> getWeatherFor(RawLocation location, LocalDate requestDate);
}
