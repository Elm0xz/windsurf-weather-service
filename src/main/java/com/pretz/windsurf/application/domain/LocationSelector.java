package com.pretz.windsurf.application.domain;

import com.pretz.windsurf.application.domain.model.LocationForecast;
import com.pretz.windsurf.application.domain.model.Forecast;

import java.util.List;
import java.util.Optional;

public interface LocationSelector {

    Optional<LocationForecast> selectOptimalLocation(List<Forecast> forecasts);
}
