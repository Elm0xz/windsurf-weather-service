package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.RawLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LocationsValidatorTest {

    private static final String MALFORMED_LOCATIONS_MESSAGE = "Locations source returned malformed locations";

    private final LocationsValidator validator = new LocationsValidator();

    @Test
    void shouldNotThrowForValidLocations() {
        var locations = List.of(
                new RawLocation("Tarifa", "ES"),
                new RawLocation("Hood River", "US"),
                new RawLocation("Le Morne", "MU")
        );

        assertThatNoException()
                .isThrownBy(() -> validator.validate(locations));
    }

    @ParameterizedTest
    @MethodSource("invalidLocations")
    void shouldThrowExceptionForMalformedLocations(List<RawLocation> locations) {
        assertThatThrownBy(() -> validator.validate(locations))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MALFORMED_LOCATIONS_MESSAGE);
    }

    private static Stream<Arguments> invalidLocations() {
        return Stream.of(
                Arguments.of((List<RawLocation>) null),
                Arguments.of(List.of()),
                Arguments.of(Stream.of(
                        new RawLocation("Tarifa", "ES"),
                        null
                ).toList()),
                Arguments.of(List.of(new RawLocation(null, "ES"))),
                Arguments.of(List.of(new RawLocation("", "ES"))),
                Arguments.of(List.of(new RawLocation("   ", "ES"))),
                Arguments.of(List.of(new RawLocation("Tarifa", null))),
                Arguments.of(List.of(new RawLocation("Tarifa", ""))),
                Arguments.of(List.of(new RawLocation("Tarifa", "   "))),
                Arguments.of(List.of(new RawLocation("Tarifa", "ESP"))),
                Arguments.of(List.of(new RawLocation("Tarifa", "es"))),
                Arguments.of(List.of(new RawLocation("Tarifa", "E1")))
        );
    }
}
