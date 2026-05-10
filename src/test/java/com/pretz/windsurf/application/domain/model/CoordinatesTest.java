package com.pretz.windsurf.application.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinatesTest {

    @Test
    void shouldCreateCoordinatesWithinValidRanges() {
        assertThatNoException()
                .isThrownBy(() -> new Coordinates(36.0143, -5.6044));
    }

    @Test
    void shouldRejectLatitudeOutsideValidRange() {
        assertThatThrownBy(() -> new Coordinates(91.0, -5.6044))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Latitude must be between -90 and 90");
    }

    @Test
    void shouldRejectLongitudeOutsideValidRange() {
        assertThatThrownBy(() -> new Coordinates(36.0143, -181.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Longitude must be between -180 and 180");
    }

    @Test
    void shouldAcceptBoundaryCoordinates() {
        assertThatNoException().isThrownBy(() -> new Coordinates(-90.0, -180.0));
        assertThatNoException().isThrownBy(() -> new Coordinates(90.0, 180.0));
    }
}
