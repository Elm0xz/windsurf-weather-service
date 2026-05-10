package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.Coordinates;
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
                rawLocation("Tarifa", "ES"),
                rawLocation("Hood River", "US"),
                rawLocation("Le Morne", "MU")
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
                        rawLocation("Tarifa", "ES"),
                        null
                ).toList()),
                Arguments.of(List.of(rawLocation(null, "ES"))),
                Arguments.of(List.of(rawLocation("", "ES"))),
                Arguments.of(List.of(rawLocation("   ", "ES"))),
                Arguments.of(List.of(rawLocation("Tarifa", null))),
                Arguments.of(List.of(rawLocation("Tarifa", ""))),
                Arguments.of(List.of(rawLocation("Tarifa", "   "))),
                Arguments.of(List.of(rawLocation("Tarifa", "ESP"))),
                Arguments.of(List.of(rawLocation("Tarifa", "es"))),
                Arguments.of(List.of(rawLocation("Tarifa", "E1"))),
                Arguments.of(List.of(new RawLocation("Tarifa", "ES", null)))
        );
    }

    private static RawLocation rawLocation(String name, String countryCode) {
        return new RawLocation(name, countryCode, new Coordinates(36.0143, -5.6044));
    }
}
