package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.pretz.windsurf.infrastructure.adapter.outbound.exception.WeatherApiClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeatherbitApiValidatorTest {

    private static final String MALFORMED_RESPONSE_MESSAGE = "Weatherbit returned malformed forecast response body";

    private final WeatherbitApiValidator validator = new WeatherbitApiValidator();

    @Test
    void shouldNotThrowForValidResponse() {
        var response = new WeatherbitApiClient.ForecastDto(List.of(
                new WeatherbitApiClient.DailyForecastDto(
                        LocalDate.of(2026, 5, 10),
                        8.5,
                        22.0
                ),
                new WeatherbitApiClient.DailyForecastDto(
                        LocalDate.of(2026, 5, 11),
                        6.2,
                        21.5
                )
        ));

        assertThatNoException()
                .isThrownBy(() -> validator.validateResponse(response));
    }

    @ParameterizedTest
    @MethodSource("malformedResponses")
    void shouldThrowExceptionForMalformedResponse(WeatherbitApiClient.ForecastDto response) {
        assertThatThrownBy(() -> validator.validateResponse(response))
                .isInstanceOf(WeatherApiClientException.class)
                .hasMessage(MALFORMED_RESPONSE_MESSAGE);
    }

    private static Stream<Arguments> malformedResponses() {
        return Stream.of(
                Arguments.of((WeatherbitApiClient.ForecastDto) null),
                Arguments.of(new WeatherbitApiClient.ForecastDto(null)),
                Arguments.of(new WeatherbitApiClient.ForecastDto(Stream.of(
                        new WeatherbitApiClient.DailyForecastDto(
                                LocalDate.of(2026, 5, 10),
                                8.5,
                                22.0
                        ),
                        null
                ).toList())),
                Arguments.of(new WeatherbitApiClient.ForecastDto(List.of(
                        new WeatherbitApiClient.DailyForecastDto(
                                null,
                                8.5,
                                22.0
                        )
                ))),
                Arguments.of(new WeatherbitApiClient.ForecastDto(List.of(
                        new WeatherbitApiClient.DailyForecastDto(
                                LocalDate.of(2026, 5, 10),
                                null,
                                22.0
                        )
                ))),
                Arguments.of(new WeatherbitApiClient.ForecastDto(List.of(
                        new WeatherbitApiClient.DailyForecastDto(
                                LocalDate.of(2026, 5, 10),
                                8.5,
                                null
                        )
                )))
        );
    }
}
