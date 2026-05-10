package com.pretz.windsurf.infrastructure.adapter.runner;

import com.pretz.windsurf.application.domain.model.Coordinates;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.SimpleApiWeatherForecastProvider;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherbitApiClient;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherbitApiValidator;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

class SimpleApiWeatherForecastProviderRunner {

    static void main(String[] args) {
        var client = RestClient.builder()
                .baseUrl("https://api.weatherbit.io")
                .build();
        String apiKey = args[0];
        int forecastDays = 7;
        String forecastPath = "/v2.0/forecast/daily";
        List<Forecast> result;
        try (SimpleApiWeatherForecastProvider provider = new SimpleApiWeatherForecastProvider(
                new WeatherbitApiClient(client, apiKey, forecastDays, forecastPath, new WeatherbitApiValidator()))) {
            result = provider.provideForecastsFor(List.of(new RawLocation("Tarifa", "ES", new Coordinates(36.0143, -5.6044)),
                            (new RawLocation("Jastarnia", "PL", new Coordinates(54.6961, 18.6787)))),
                    LocalDate.now());
        }

        System.out.println(result);
    }
}
