package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;

import java.time.LocalDate;
import java.util.List;

public interface WeatherApiClient {

    List<Forecast> getLongtermForecastFor(RawLocation location, LocalDate requestDate);
}
