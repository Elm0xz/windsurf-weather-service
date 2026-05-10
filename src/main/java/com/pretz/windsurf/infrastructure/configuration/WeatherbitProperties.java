package com.pretz.windsurf.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "windsurf.weatherbit")
public record WeatherbitProperties(
        String baseUrl,
        String apiKey,
        int forecastDays,
        String forecastPath
) {
}
