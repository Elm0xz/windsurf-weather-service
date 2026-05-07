package com.pretz.windsurf.application.domain.port;

import com.pretz.windsurf.application.domain.model.Location;

import java.util.List;

public interface LocationsProviderPort {

    public List<Location> provideLocations();
}
