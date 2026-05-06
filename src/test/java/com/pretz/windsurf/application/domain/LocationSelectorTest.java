package com.pretz.windsurf.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class LocationSelectorTest {

    @Test
    public void shouldReturnOptimalLocation() {
        var locationSelector = getLocationSelectorUnderTest();

        var locations = List.of(
                new Location("Jastarnia", "PL", 25.2, 10.3),
                new Location("Bridgetown", "BB", 16.3, 33.2),
                new Location("Fortaleza", "BR", 9.9, 35.2),
                new Location("Pissouri", "CY", 17.7, 29.3),
                new Location("Le Morne", "MU", 30.2, 27.3)
        );

        var result = locationSelector.selectOptimalLocation(locations);
        Assertions.assertThat(result).isEqualTo(Optional.of(new Location("Pissouri", "CY", 17.7, 29.3)));
    }

    @Test
    public void shouldReturnNoLocationIfNoneMeetsCriteria() {
        var locationSelector = getLocationSelectorUnderTest();

        var locations = List.of(
                new Location("Jastarnia", "PL", 25.2, 10.3),
                new Location("Bridgetown", "BB", 3.3, 23.5),
                new Location("Fortaleza", "BR", 9.9, 35.2)
        );

        var result = locationSelector.selectOptimalLocation(locations);
        Assertions.assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnNoLocationIfNoLocationPassed() {
        var locationSelector = getLocationSelectorUnderTest();

        List<Location> locations = List.of();

        var result = locationSelector.selectOptimalLocation(locations);
        Assertions.assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnFirstOptimalLocationInCaseMoreThanOneIsOptimal() {
        var locationSelector = getLocationSelectorUnderTest();

        var locations = List.of(
                new Location("Jastarnia", "PL", 15.0, 25.0),
                new Location("Bridgetown", "BB", 15.0, 25.0),
                new Location("Fortaleza", "BR", 9.9, 35.2)
        );

        var result = locationSelector.selectOptimalLocation(locations);
        Assertions.assertThat(result).isEqualTo(Optional.of(new Location("Jastarnia", "PL", 15.0, 25.0)));
    }

    private LocationSelector getLocationSelectorUnderTest() {
        return new BaseLocationSelector();
    }
}
