package com.pretz.windsurf.infrastructure.adapter.runner;

import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.WeatherbitApiClient;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

class WeatherbitApiClientRunner {

    static void main() {
        var client = RestClient.builder()
                .baseUrl("https://api.weatherbit.io")
                .build();
        var result = new WeatherbitApiClient(client).getLongtermForecastFor(new RawLocation("Łódź", "PL"), LocalDate.now());

        System.out.println(result);
    }
}
