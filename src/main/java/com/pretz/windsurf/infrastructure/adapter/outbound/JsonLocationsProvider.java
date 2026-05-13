package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.outbound.LocationsProviderPort;
import com.pretz.windsurf.application.port.outbound.exception.LocationsUnavailableException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Service
public class JsonLocationsProvider implements LocationsProviderPort {

    private static final Logger log = LoggerFactory.getLogger(JsonLocationsProvider.class);

    private final String locationsSourceName;
    private final ObjectMapper mapper;
    private final LocationsValidator locationsValidator;
    private List<RawLocation> locations;

    public JsonLocationsProvider(@Value("${windsurf.locations.source-name}") String locationsSourceName,
                                 ObjectMapper mapper, LocationsValidator locationsValidator) {
        this.locationsSourceName = locationsSourceName;
        this.mapper = mapper;
        this.locationsValidator = locationsValidator;
    }

    @PostConstruct
    void init() {
        locations = loadLocations();
    }

    @Override
    public List<RawLocation> provideLocations() {
        return List.copyOf(locations);
    }

    private List<RawLocation> loadLocations() {
        try (InputStream locationsStream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(locationsSourceName))) {
            List<RawLocation> locations = mapper.readValue(locationsStream, new TypeReference<>() {
            });
            locationsValidator.validate(locations);
            log.debug("Loaded locations from {}: {}", locationsSourceName, locations);
            return locations;
        } catch (Exception exception) {
            throw new LocationsUnavailableException("Could not provide locations from resource: " + locationsSourceName,
                    exception);
        }
    }
}
