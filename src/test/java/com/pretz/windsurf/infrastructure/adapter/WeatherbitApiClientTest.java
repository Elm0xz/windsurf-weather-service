package com.pretz.windsurf.infrastructure.adapter;

import com.pretz.windsurf.application.domain.model.RawLocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class WeatherbitApiClientTest {

    @Test
    void shouldReturnAnswer() {
        var result = new WeatherbitApiClient().getWeatherFor(new RawLocation("Łódź", "PL"), LocalDate.now());

        Assertions.assertThat(result).isEqualTo("dupa123");
    }
}
