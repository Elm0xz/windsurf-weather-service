package com.pretz.windsurf.infrastructure.adapter;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.WeatherForecastProviderPort;

import java.time.LocalDate;
import java.util.List;

public class SimpleApiWeatherForecastProvider implements WeatherForecastProviderPort {

    private final WeatherApiClient apiClient;

    public SimpleApiWeatherForecastProvider(WeatherApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Forecast> getForecastsFor(List<RawLocation> locations, LocalDate requestDate) {

        return locations.parallelStream().map(location -> apiClient.getWeatherFor(location, requestDate))
                .flatMap(List::stream)
                .filter(fc -> requestDate.equals(fc.forecastDate()))
                .toList();
        //TODO 3. Error handling
    }
}
