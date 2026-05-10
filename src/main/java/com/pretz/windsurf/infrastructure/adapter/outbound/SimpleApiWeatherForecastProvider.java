package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.application.port.outbound.WeatherForecastProviderPort;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SimpleApiWeatherForecastProvider implements WeatherForecastProviderPort, AutoCloseable {

    private final WeatherApiClient apiClient;
    private final ExecutorService executorService;

    public SimpleApiWeatherForecastProvider(WeatherApiClient apiClient) {
        this.apiClient = apiClient;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public List<Forecast> provideForecastsFor(List<RawLocation> locations, LocalDate requestDate) {
        //TODO validation on locations?
        Objects.requireNonNull(locations, "locations must not be null");
        Objects.requireNonNull(requestDate, "requestDate must not be null");

        var futures = locations.stream()
                .filter(Objects::nonNull)
                .map(location -> CompletableFuture.supplyAsync(
                        () -> apiClient.getLongtermForecastFor(location, requestDate),
                        executorService
                )).toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .filter(fc -> requestDate.equals(fc.forecastDate()))
                    .toList();
        } catch (CompletionException | CancellationException exception) {
            throw new ForecastProviderUnavailableException(
                    "Could not fetch weather forecasts",
                    exception.getCause());
        }
    }

    @Override
    @PreDestroy
    public void close() {
        executorService.shutdown();
    }
}
