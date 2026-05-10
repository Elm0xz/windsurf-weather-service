package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.RawLocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class LocationsValidator {

    private static final String MALFORMED_LOCATIONS_MESSAGE = "Locations source returned malformed locations";
    private static final String COUNTRY_CODE_PATTERN = "[A-Z]{2}";

    void validate(List<RawLocation> locations) {
        if (locations == null || locations.isEmpty() || locations.stream().anyMatch(this::isInvalid)) {
            throw new IllegalArgumentException(MALFORMED_LOCATIONS_MESSAGE);
        }
    }

    private boolean isInvalid(RawLocation location) {
        return location == null
                || isBlank(location.name())
                || isBlank(location.countryCode())
                || !location.countryCode().matches(COUNTRY_CODE_PATTERN)
                || location.coordinates() == null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
