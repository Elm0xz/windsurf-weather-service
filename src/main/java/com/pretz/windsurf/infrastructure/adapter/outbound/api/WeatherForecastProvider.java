package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class WeatherForecastProvider implements WeatherForecastProviderPort, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(WeatherForecastProvider.class);

    private final WeatherApiClient apiClient;
    private final ExecutorService executorService;
    private final Cache<LocalDate, List<Forecast>> dailyForecastsCache;

    public WeatherForecastProvider(WeatherApiClient apiClient, Cache<LocalDate, List<Forecast>> dailyForecastsCache,
                                   @Value("${windsurf.forecast.executor.pool-size}")int poolSize) {
        this.apiClient = apiClient;
        this.dailyForecastsCache = dailyForecastsCache;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public List<Forecast> provideForecastsFor(List<RawLocation> locations, LocalDate requestDate) {
        validateInput(locations, requestDate);

        // Concurrent cache misses may trigger duplicate API calls but this is acceptable for now due to low traffic and the cache TTL
        List<Forecast> dailyForecast = dailyForecastsCache.getIfPresent(requestDate);
        if (dailyForecast != null) {
            return dailyForecast;
        }

        return getForecastsFromApiAndAddToCache(locations, requestDate);
    }

    private List<Forecast> getForecastsFromApiAndAddToCache(List<RawLocation> locations, LocalDate requestDate) {
        List<Forecast> forecasts = getForecastsFromApi(locations);
        var forecastsByDay = forecasts.stream()
                .collect(Collectors.groupingBy(Forecast::forecastDate));
        dailyForecastsCache.putAll(forecastsByDay);

        return forecasts.stream()
                .filter(fc -> requestDate.equals(fc.forecastDate()))
                .toList();
    }

    private List<Forecast> getForecastsFromApi(List<RawLocation> locations) {
        var futures = locations.stream()
                .map(location -> CompletableFuture.supplyAsync(
                        () -> apiClient.getLongtermForecastFor(location),
                        executorService
                )).toList();

        try {
            List<Forecast> forecasts = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();

            log.debug("Fetched forecasts: {}", forecasts);

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
