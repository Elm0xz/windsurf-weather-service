package com.pretz.windsurf.application.port;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;

import java.time.LocalDate;
import java.util.List;

public interface WeatherForecastProviderPort {

    List<Forecast> getForecastsFor(List<RawLocation> locations, LocalDate date);
}
