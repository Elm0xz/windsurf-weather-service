package com.pretz.windsurf.infrastructure.adapter.runner;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.SimpleApiWeatherForecastProvider;
import com.pretz.windsurf.infrastructure.adapter.WeatherbitApiClient;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

class SimpleApiWeatherForecastProviderRunner {

    static void main() {
        var client = RestClient.builder()
                .baseUrl("https://api.weatherbit.io")
                .build();

        List<Forecast> result;
        try (SimpleApiWeatherForecastProvider provider = new SimpleApiWeatherForecastProvider(new WeatherbitApiClient(client))) {
            result = provider.getForecastsFor(List.of(new RawLocation("Tarifa", "ES"),
                            (new RawLocation("Jastarnia", "PL"))),
                    LocalDate.now());
        }

        System.out.println(result);
    }
}
