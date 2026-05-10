package com.pretz.windsurf.infrastructure.configuration;

import com.pretz.windsurf.application.domain.service.BaseLocationSelector;
import com.pretz.windsurf.application.domain.validation.ForecastDateValidator;
import com.pretz.windsurf.application.domain.service.LocationSelector;
import com.pretz.windsurf.application.domain.WindsurfWeatherService;
import com.pretz.windsurf.application.port.inbound.WindsurfWeatherPort;
import com.pretz.windsurf.application.port.outbound.LocationsProviderPort;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(WeatherbitProperties.class)
public class WindsurfWeatherConfig {

    @Bean
    WindsurfWeatherPort windsurfWeatherPort(
            LocationsProviderPort locationsProvider,
            WeatherForecastProviderPort weatherForecastProvider,
            LocationSelector locationSelector,
            ForecastDateValidator validator
    ) {
        return new WindsurfWeatherService(
                locationsProvider,
                weatherForecastProvider,
                locationSelector,
                validator
        );
    }

    @Bean
    LocationSelector locationSelector() {
        return new BaseLocationSelector();
    }

    @Bean
    ForecastDateValidator forecastDateValidator() {
        return new ForecastDateValidator();
    }

    @Bean
    public RestClient restClient(WeatherbitProperties weatherbitProperties) {
        //TODO path to config
        return RestClient.builder()
                .baseUrl(weatherbitProperties.baseUrl())
                .build();
    }
}
