package com.pretz.windsurf.application.port;

import com.pretz.windsurf.application.domain.model.RawLocation;

import java.util.List;

public interface LocationsProviderPort {

    List<RawLocation> provideLocations();
}
