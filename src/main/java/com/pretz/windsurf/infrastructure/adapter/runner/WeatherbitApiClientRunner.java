package com.pretz.windsurf.infrastructure.adapter.runner;

import com.pretz.windsurf.application.domain.model.Coordinates;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherbitApiClient;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherbitApiValidator;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

class WeatherbitApiClientRunner {

    static void main(String[] args) {
        var client = RestClient.builder()
                .baseUrl("https://api.weatherbit.io")
                .build();
        String apiKey = args[0];
        int forecastDays = 7;
        String forecastPath = "/v2.0/forecast/daily";
        var result = new WeatherbitApiClient(client, apiKey, forecastDays, forecastPath, new WeatherbitApiValidator())
                .getLongtermForecastFor(new RawLocation("Le Morne", "MU", new Coordinates(-20.4561, 57.3139)), LocalDate.now());

        System.out.println(result);
    }
}
