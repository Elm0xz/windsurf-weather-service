package com.pretz.windsurf.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(WeatherbitProperties.class)
public class WindsurfWeatherConfig {

    @Bean
    public RestClient restClient(WeatherbitProperties weatherbitProperties) {
        //TODO path to config
        return RestClient.builder()
                .baseUrl(weatherbitProperties.baseUrl())
                .build();
    }
}
