package com.pretz.windsurf.infrastructure.adapter.runner;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.SimpleApiWeatherForecastProvider;
import com.pretz.windsurf.infrastructure.adapter.outbound.WeatherbitApiClient;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

class SimpleApiWeatherForecastProviderRunner {

    static void main() {
        var client = RestClient.builder()
                .baseUrl("https://api.weatherbit.io")
                .build();
        //TODO remove this
        String apiKey = "2f2da76fe6674f6293a9ff2f04981556";
        int forecastDays = 7;
        String forecastPath = "/v2.0/forecast/daily";
        List<Forecast> result;
        try (SimpleApiWeatherForecastProvider provider = new SimpleApiWeatherForecastProvider(
                new WeatherbitApiClient(client, apiKey, forecastDays, forecastPath))) {
            result = provider.provideForecastsFor(List.of(new RawLocation("Tarifa", "ES"),
                            (new RawLocation("Jastarnia", "PL"))),
                    LocalDate.now());
        }

        System.out.println(result);
    }
}
