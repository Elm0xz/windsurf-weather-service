package com.pretz.windsurf.infrastructure.adapter;

import com.pretz.windsurf.application.domain.model.RawLocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class SimpleApiWeatherForecastProviderTest {

    private final SimpleApiWeatherForecastProvider provider = new SimpleApiWeatherForecastProvider(new WeatherbitApiClient());

    @Test
    void shouldRun() {

        var result = provider.getForecastsFor(List.of(new RawLocation("Tarifa", "ES"),
                        (new RawLocation("Jastarnia", "PL"))),
                LocalDate.now());

        Assertions.assertThat(result).isEqualTo("dupa123");

    }
}