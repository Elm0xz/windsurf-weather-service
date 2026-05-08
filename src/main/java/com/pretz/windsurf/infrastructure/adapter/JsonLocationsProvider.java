package com.pretz.windsurf.infrastructure.adapter;

import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.LocationsProviderPort;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class JsonLocationsProvider implements LocationsProviderPort {

    private final String locationsSourceName;
    private final ObjectMapper mapper;

    public JsonLocationsProvider(String locationsSourceName, ObjectMapper mapper) {
        this.locationsSourceName = locationsSourceName;
        this.mapper = mapper;
    }

    @Override
    //TODO caching locations
    public List<RawLocation> provideLocations() {
        try (InputStream locationsStream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(locationsSourceName))) {
            return mapper.readValue(locationsStream, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new LocationsProviderException("Could not provide locations from resource: " + locationsSourceName,
                    exception);
        }
    }
}
