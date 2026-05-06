package com.pretz.windsurf.application.domain;

import java.util.List;
import java.util.Optional;

public class BaseLocationSelector implements LocationSelector {

    @Override
    public Optional<Location> selectOptimalLocation(List<Location> locations) {
        return locations.stream()
                .filter(this::isWindInRange)
                .filter(this::isTemperatureInRange)
                .max(this::compareValues);
    }

    private boolean isWindInRange(Location loc) {
        return loc.windSpeed() >= 5.0 && loc.windSpeed() <= 18.0;
    }

    private boolean isTemperatureInRange(Location loc) {
        return loc.temperature() >= 5.0 && loc.temperature() <= 35.0;
    }

    private int compareValues(Location loc1, Location loc2) {
        return (int) Math.signum(loc1.windSpeed() * 3 + loc1.temperature() - loc2.windSpeed() * 3 - loc2.temperature());
    }
}
