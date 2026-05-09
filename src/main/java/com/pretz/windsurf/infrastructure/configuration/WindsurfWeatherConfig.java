package com.pretz.windsurf.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WindsurfWeatherConfig {

    @Bean
    public RestClient restClient(@Value("${windsurf.weatherbit.base-url}") String weatherbitBaseUrl) {
        //TODO path to config
        return RestClient.builder()
                .baseUrl(weatherbitBaseUrl)
                .build();
    }
}