package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.exception.LocationsProviderException;
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
                new ObjectMapper());

        List<RawLocation> locations = provider.provideLocations();

        Assertions.assertThat(locations)
                .hasSize(3)
                .extracting(RawLocation::name, RawLocation::countryCode)
                .containsExactly(
                        Assertions.tuple("Tarifa", "ES"),
                        Assertions.tuple("Hood River", "US"),
                        Assertions.tuple("Le Morne", "MU")
                );
    }

    @ParameterizedTest
    @MethodSource("provideFailingLocationsSources")
    void shouldFailProvidingLocations(String locationsSource) {
        var provider = new JsonLocationsProvider(locationsSource,
                new ObjectMapper());

        Assertions.assertThatExceptionOfType(LocationsProviderException.class)
                .isThrownBy(provider::provideLocations)
                .withMessage("Could not provide locations from resource: %s".formatted(locationsSource));
    }

    public static Stream<Arguments> provideFailingLocationsSources() {
        return Stream.of(Arguments.of("invalidlocations.json"),
                Arguments.of("missinglocations.json"));
    }
}
