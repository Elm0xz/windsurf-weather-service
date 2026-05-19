package com.pretz.windsurf.infrastructure.adapter.outbound.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pretz.windsurf.application.domain.model.Coordinates;
import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeatherForecastProviderTest {

    private final WeatherApiClient apiClient = mock(WeatherApiClient.class);
    private final Cache<LocalDate, List<Forecast>> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12))
            .build();
    private final WeatherForecastProvider provider = new WeatherForecastProvider(apiClient, cache, 5);

    @AfterEach
    void tearDown() {
        provider.close();
        cache.invalidateAll();
    }

    @Test
    void shouldReturnOnlyForecastsForRequestedDate() {
        var requestedDate = LocalDate.of(2026, 5, 10);
        var location = locationTarifa();

        var matchingForecast = new Forecast(location, requestedDate, 8.5, 22.0);
        var otherDayForecast1 = new Forecast(location, requestedDate.plusDays(1), 6.0, 21.0);
        var otherDayForecast2 = new Forecast(location, requestedDate.plusDays(2), 12.0, 24.2);

        when(apiClient.getLongtermForecastFor(location))
                .thenReturn(List.of(matchingForecast, otherDayForecast1, otherDayForecast2));

        var result = provider.provideForecastsFor(List.of(location), requestedDate);

        assertThat(result).containsExactly(matchingForecast);
    }

    @Test
    void shouldAggregateForecastsFromMultipleLocations() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var firstLocation = locationTarifa();
        var secondLocation = locationJastarnia();

        var firstLocationForecast = new Forecast(firstLocation, requestedDate, 8.5, 22.0);
        var secondLocationForecast = new Forecast(secondLocation, requestedDate, 7.0, 18.0);

        when(apiClient.getLongtermForecastFor(firstLocation)).thenReturn(List.of(firstLocationForecast));
        when(apiClient.getLongtermForecastFor(secondLocation)).thenReturn(List.of(secondLocationForecast));

        var result = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate);

        assertThat(result).containsExactly(firstLocationForecast, secondLocationForecast);
    }

    @Test
    void shouldAggregateAndReturnOnlyForecastsForRequestedDateForMultipleLocations() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var firstLocation = locationTarifa();
        var secondLocation = locationJastarnia();

        var firstLocationForecastOnRequestedDay = new Forecast(firstLocation,
                requestedDate, 8.5, 22.0);
        var firstLocationForecastOnOtherDay = new Forecast(firstLocation,
                requestedDate.plusDays(1), 8.5, 22.0);
        var secondLocationForecastOnRequestedDay = new Forecast(secondLocation,
                requestedDate, 7.0, 18.0);
        var secondLocationForecastOnOtherDay = new Forecast(secondLocation,
                requestedDate.plusDays(1), 8.5, 22.0);

        when(apiClient.getLongtermForecastFor(firstLocation))
                .thenReturn(List.of(firstLocationForecastOnRequestedDay, firstLocationForecastOnOtherDay));
        when(apiClient.getLongtermForecastFor(secondLocation))
                .thenReturn(List.of(secondLocationForecastOnRequestedDay, secondLocationForecastOnOtherDay));

        var result = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate);

        assertThat(result).containsExactly(firstLocationForecastOnRequestedDay, secondLocationForecastOnRequestedDay);
    }

    @Test
    void shouldCallApiClientForEachLocation() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var location1 = locationTarifa();
        var location2 = locationJastarnia();

        when(apiClient.getLongtermForecastFor(location1)).thenReturn(List.of());
        when(apiClient.getLongtermForecastFor(location2)).thenReturn(List.of());

        provider.provideForecastsFor(List.of(location1, location2), requestedDate);

        verify(apiClient).getLongtermForecastFor(location1);
        verify(apiClient).getLongtermForecastFor(location2);
    }

    @Test
    void shouldReturnCachedForecastsWhenRequestedDateIsAlreadyCached() {
        var requestedDate = LocalDate.of(2026, 5, 10);
        var location = locationTarifa();

        var cachedForecast = new Forecast(location, requestedDate, 8.5, 22.0);
        cache.put(requestedDate, List.of(cachedForecast));

        var result = provider.provideForecastsFor(List.of(location), requestedDate);

        assertThat(result).containsExactly(cachedForecast);
        verify(apiClient, never()).getLongtermForecastFor(location);
    }

    @Test
    void shouldUseCachedForecastsForRequestInForecastWindow() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var firstLocation = locationTarifa();
        var secondLocation = locationJastarnia();

        var firstLocationForecast1 = new Forecast(firstLocation, requestedDate, 8.5, 22.0);
        var firstLocationForecast2 = new Forecast(firstLocation, requestedDate.plusDays(1), 9.5, 23.0);
        var secondLocationForecast1 = new Forecast(secondLocation, requestedDate, 7.0, 18.0);
        var secondLocationForecast2 = new Forecast(secondLocation, requestedDate.plusDays(1), 8.5, 19.0);

        when(apiClient.getLongtermForecastFor(firstLocation)).thenReturn(List.of(firstLocationForecast1, firstLocationForecast2));
        when(apiClient.getLongtermForecastFor(secondLocation)).thenReturn(List.of(secondLocationForecast1, secondLocationForecast2));

        var firstResult = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate);
        var secondResult = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate.plusDays(1));

        assertThat(firstResult).containsExactly(firstLocationForecast1, secondLocationForecast1);
        assertThat(secondResult).containsExactly(firstLocationForecast2, secondLocationForecast2);

        verify(apiClient, times(1)).getLongtermForecastFor(firstLocation);
        verify(apiClient, times(1)).getLongtermForecastFor(secondLocation);
    }


    private RawLocation locationTarifa() {
        return new RawLocation("Tarifa", "ES", new Coordinates(36.0143, -5.6044));
    }

    private RawLocation locationJastarnia() {
        return new RawLocation("Jastarnia", "PL", new Coordinates(54.6961, 18.6787));
    }
}
