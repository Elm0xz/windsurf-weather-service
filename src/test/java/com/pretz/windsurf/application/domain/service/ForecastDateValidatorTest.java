package com.pretz.windsurf.application.domain.service;

import com.pretz.windsurf.application.domain.validation.ForecastDateValidator;
import com.pretz.windsurf.application.domain.validation.InvalidForecastDateException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class ForecastDateValidatorTest {

    private static final LocalDate TODAY = LocalDate.now();
    private final ForecastDateValidator validator = new ForecastDateValidator();

    @ParameterizedTest()
    @MethodSource("provideInvalidDates")
    void shouldThrowOnDatesOutsideForecastRange(LocalDate date) {

        Assertions.assertThatExceptionOfType(InvalidForecastDateException.class)
                .isThrownBy(() -> validator.validate(date))
                .withMessage("Forecast date should be between today and 7 days from today");
    }

    @ParameterizedTest()
    @MethodSource("provideValidDates")
    void shouldNotThrowOnDatesInsideForecastRange(LocalDate date) {

        Assertions.assertThatNoException().isThrownBy(() -> validator.validate(date));
    }

    @Test
    void shouldThrowOnNullDate() {

        Assertions.assertThatExceptionOfType(InvalidForecastDateException.class)
                .isThrownBy(() -> validator.validate(null))
                .withMessage("Forecast date must not be null");
    }

    private static Stream<Arguments> provideInvalidDates() {
        return Stream.of(
                Arguments.of(TODAY.plusDays(8)),
                Arguments.of(TODAY.plusDays(15)),
                Arguments.of(TODAY.plusDays(25)),
                Arguments.of(TODAY.minusDays(3)),
                Arguments.of(TODAY.minusDays(12))
        );
    }

    private static Stream<Arguments> provideValidDates() {
        return Stream.of(
                Arguments.of(TODAY),
                Arguments.of(TODAY.plusDays(1)),
                Arguments.of(TODAY.plusDays(2)),
                Arguments.of(TODAY.plusDays(3)),
                Arguments.of(TODAY.plusDays(7))
        );
    }
}
