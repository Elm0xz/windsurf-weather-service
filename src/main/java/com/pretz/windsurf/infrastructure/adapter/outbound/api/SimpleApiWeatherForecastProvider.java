package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO [WIND-6] Caching proxy
@Service
public class SimpleApiWeatherForecastProvider implements WeatherForecastProviderPort, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(SimpleApiWeatherForecastProvider.class);

    private final WeatherApiClient apiClient;
    private final ExecutorService executorService;

    public SimpleApiWeatherForecastProvider(WeatherApiClient apiClient) {
        this.apiClient = apiClient;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public List<Forecast> provideForecastsFor(List<RawLocation> locations, LocalDate requestDate) {
        validateInput(locations, requestDate);

        var futures = locations.stream()
                .map(location -> CompletableFuture.supplyAsync(
                        () -> apiClient.getLongtermForecastFor(location, requestDate),
                        executorService
                )).toList();

        try {
            List<Forecast> forecasts = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .filter(fc -> requestDate.equals(fc.forecastDate()))
                    .toList();

            log.debug("Fetched forecasts for date {}: {}", requestDate, forecasts);

            return forecasts;
        } catch (CompletionException | CancellationException exception) {
            throw new ForecastProviderUnavailableException(
                    "Could not fetch weather forecasts",
                    exception.getCause());
        }
    }

    private void validateInput(List<RawLocation> locations, LocalDate requestDate) {
        Objects.requireNonNull(locations, "locations must not be null");
        Objects.requireNonNull(requestDate, "requestDate must not be null");

        if (locations.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("locations must not contain null elements");
        }
    }

    @Override
    @PreDestroy
    public void close() {
        executorService.shutdown();
    }
}
