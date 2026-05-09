package com.pretz.windsurf.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

public class WeatherbitApiClient implements WeatherApiClient {

    //TODO both to configs and maybe anonymize or set as parameter for application run
    private static final String API_KEY = "2f2da76fe6674f6293a9ff2f04981556";
    private static final int FORECAST_DAYS = 7;
    private static final String FORECAST_PATH = "/v2.0/forecast/daily";

    private final RestClient restClient;

    public WeatherbitApiClient(RestClient restClient) {
        this.restClient = restClient;
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
                        .path(FORECAST_PATH)
                        .queryParam("key", API_KEY)
                        .queryParam("days", FORECAST_DAYS)
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
