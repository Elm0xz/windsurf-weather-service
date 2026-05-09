package com.pretz.windsurf.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

@Service
public class WeatherbitApiClient implements WeatherApiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final int forecastDays;
    private final String forecastPath;

    public WeatherbitApiClient(RestClient restClient,
                               @Value("${windsurf.weatherbit.api-key}") String apiKey,
                               @Value("${windsurf.weatherbit.forecast-days}") int forecastDays,
                               @Value("${windsurf.weatherbit.forecast-path}") String forecastPath) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.forecastDays = forecastDays;
        this.forecastPath = forecastPath;
    }

    @Override
    public List<Forecast> getLongtermForecastFor(RawLocation location, LocalDate requestDate) {
        try {
            ForecastDto response = fetchForecasts(location);
            validateResponse(response);

            return mapResponse(location, requestDate, response);
        } catch (RestClientException exception) {
            throw new WeatherApiClientException("Could not fetch long-term forecast from Weatherbit", exception);
        }
    }

    private void validateResponse(ForecastDto response) {
        if (response == null || response.data() == null) {
            throw new WeatherApiClientException("Weatherbit returned malformed forecast response body");
        }
    }

    private ForecastDto fetchForecasts(RawLocation location) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(forecastPath)
                        .queryParam("key", apiKey)
                        .queryParam("days", forecastDays)
                        .queryParam("city", location.name())
                        .queryParam("country", location.countryCode())
                        .build())
                .retrieve()
                .body(ForecastDto.class);
    }

    private List<Forecast> mapResponse(RawLocation location, LocalDate requestDate, ForecastDto response) {
        return response.data().stream()
                .map(dailyForecastDto -> new Forecast(
                        location,
                        requestDate,
                        dailyForecastDto.forecastDay(),
                        dailyForecastDto.windSpeed(),
                        dailyForecastDto.temperature()
                ))
                .toList();
    }

    private record ForecastDto(List<DailyForecastDto> data) {
    }

    private record DailyForecastDto(
            @JsonProperty("valid_date") LocalDate forecastDay,
            @JsonProperty("wind_spd") Double windSpeed,
            @JsonProperty("temp") Double temperature
    ) {
    }
}
