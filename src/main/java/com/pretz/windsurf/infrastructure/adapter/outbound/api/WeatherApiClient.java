package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;

import java.util.List;

public interface WeatherApiClient {

    List<Forecast> getLongtermForecastFor(RawLocation location);
}
