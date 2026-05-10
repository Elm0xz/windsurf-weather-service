package com.pretz.windsurf.application.port.outbound;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;

import java.time.LocalDate;
import java.util.List;

public interface WeatherForecastProviderPort {

    List<Forecast> provideForecastsFor(List<RawLocation> locations, LocalDate date);
}
