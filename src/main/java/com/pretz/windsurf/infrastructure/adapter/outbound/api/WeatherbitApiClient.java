package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.exception.WeatherApiClientException;
import com.pretz.windsurf.infrastructure.configuration.WeatherbitProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WeatherbitApiValidator validator;

    @Autowired
    public WeatherbitApiClient(RestClient restClient, WeatherbitProperties weatherbitProperties, WeatherbitApiValidator validator) {
        this(restClient,
                weatherbitProperties.apiKey(),
                weatherbitProperties.forecastDays(),
                weatherbitProperties.forecastPath(),
                validator);
    }

    public WeatherbitApiClient(RestClient restClient,
                               String apiKey,
                               int forecastDays,
                               String forecastPath,
                               WeatherbitApiValidator validator) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.forecastDays = forecastDays;
        this.forecastPath = forecastPath;
        this.validator = validator;
    }

    @Override
    public List<Forecast> getLongtermForecastFor(RawLocation location, LocalDate requestDate) {
        try {
            ForecastDto response = fetchForecasts(location);
            validator.validateResponse(response);

            return mapResponse(location, requestDate, response);
        } catch (RestClientException exception) {
            throw new WeatherApiClientException("Could not fetch long-term forecast from Weatherbit", exception);
        }
    }

    private ForecastDto fetchForecasts(RawLocation location) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(forecastPath)
                        .queryParam("key", apiKey)
                        .queryParam("days", forecastDays)
                        .queryParam("lat", location.coordinates().latitude())
                        .queryParam("lon", location.coordinates().longitude())
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

    record ForecastDto(List<DailyForecastDto> data) {
    }

    record DailyForecastDto(
            @JsonProperty("valid_date") LocalDate forecastDay,
            @JsonProperty("wind_spd") Double windSpeed,
            @JsonProperty("temp") Double temperature
    ) {
    }
}
