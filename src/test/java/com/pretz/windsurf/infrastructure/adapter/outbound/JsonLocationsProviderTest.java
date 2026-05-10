package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.Coordinates;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.outbound.exception.LocationsUnavailableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

class JsonLocationsProviderTest {

    @Test
    void shouldProvideLocationsListFromJson() {
        var provider = new JsonLocationsProvider("testlocations.json",
                new ObjectMapper(), new LocationsValidator());

        List<RawLocation> locations = provider.provideLocations();

        Assertions.assertThat(locations)
                .hasSize(3)
                .extracting(RawLocation::name, RawLocation::countryCode, RawLocation::coordinates)
                .containsExactly(
                        Assertions.tuple("Tarifa", "ES", new Coordinates(36.0143, -5.6044)),
                        Assertions.tuple("Hood River", "US", new Coordinates(45.7054, -121.5215)),
                        Assertions.tuple("Le Morne", "MU", new Coordinates(-20.4561, 57.3139))
                );
    }

    @ParameterizedTest
    @MethodSource("failingLocationSources")
    void shouldFailProvidingLocations(String locationsSource) {
        var provider = new JsonLocationsProvider(locationsSource,
                new ObjectMapper(), new LocationsValidator());

        Assertions.assertThatExceptionOfType(LocationsUnavailableException.class)
                .isThrownBy(provider::provideLocations)
                .withMessage("Could not provide locations from resource: %s".formatted(locationsSource));
    }

    private static Stream<Arguments> failingLocationSources() {
        return Stream.of(
                Arguments.of("missinglocations.json"),
                Arguments.of("malformedlocations.json"),
                Arguments.of("invalidlocations.json")
        );
    }
}
