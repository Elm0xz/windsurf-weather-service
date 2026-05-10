package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.pretz.windsurf.infrastructure.adapter.outbound.exception.WeatherApiClientException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WeatherbitApiValidator {

    private static final String MALFORMED_RESPONSE_MESSAGE = "Weatherbit returned malformed forecast response body";

    public WeatherbitApiValidator() {
    }

    void validateResponse(WeatherbitApiClient.ForecastDto response) {
        if (response == null
                || response.data() == null
                || response.data().stream().anyMatch(this::isInvalid)) {
            throw new WeatherApiClientException(MALFORMED_RESPONSE_MESSAGE);
        }
    }

    private boolean isInvalid(WeatherbitApiClient.DailyForecastDto dailyForecastDto) {
        return dailyForecastDto == null
                || dailyForecastDto.forecastDay() == null
                || dailyForecastDto.windSpeed() == null
                || dailyForecastDto.temperature() == null;
    }
}
