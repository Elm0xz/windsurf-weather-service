package com.pretz.windsurf.application.domain;

import java.util.List;
import java.util.Optional;

public interface LocationSelector {

    Optional<Location> selectOptimalLocation(List<Location> locations);
}
