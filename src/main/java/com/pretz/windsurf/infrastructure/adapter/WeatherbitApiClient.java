package com.pretz.windsurf.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

public class WeatherbitApiClient implements WeatherApiClient {

    //TODO to configs and maybe anonymize or set as parameter for application run
    public static final String API_KEY = "2f2da76fe6674f6293a9ff2f04981556";
    public static final int FORECAST_DAYS = 7;

    @Override
    public List<Forecast> getWeatherFor(RawLocation location, LocalDate requestDate) {
        var restClient = RestClient.create();
        ForecastDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.weatherbit.io")
                        .path("/v2.0/forecast/daily")
                        .queryParam("key", API_KEY)
                        .queryParam("days", FORECAST_DAYS)
                        .queryParam("city", location.name())
                        .queryParam("country", location.countryCode())
                        .build())
                .retrieve()
                .body(ForecastDto.class);

        //TODO more error handling
        if (response == null || response.data() == null) {
            return List.of();
        }

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
