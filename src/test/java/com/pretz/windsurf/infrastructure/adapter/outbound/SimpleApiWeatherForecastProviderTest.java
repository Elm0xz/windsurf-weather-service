package com.pretz.windsurf.infrastructure.adapter.outbound;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SimpleApiWeatherForecastProviderTest {

    private final WeatherApiClient apiClient = mock(WeatherApiClient.class);
    private final SimpleApiWeatherForecastProvider provider = new SimpleApiWeatherForecastProvider(apiClient);

    @AfterEach
    void tearDown() {
        provider.close();
    }

    @Test
    void shouldReturnOnlyForecastsForRequestedDate() {
        var requestedDate = LocalDate.of(2026, 5, 10);
        var location = new RawLocation("Tarifa", "ES");

        var matchingForecast = new Forecast(location, requestedDate, requestedDate, 8.5, 22.0);
        var otherDayForecast1 = new Forecast(location, requestedDate, requestedDate.plusDays(1), 6.0, 21.0);
        var otherDayForecast2 = new Forecast(location, requestedDate, requestedDate.plusDays(2), 12.0, 24.2);

        when(apiClient.getLongtermForecastFor(location, requestedDate))
                .thenReturn(List.of(matchingForecast, otherDayForecast1, otherDayForecast2));

        var result = provider.provideForecastsFor(List.of(location), requestedDate);

        assertThat(result).containsExactly(matchingForecast);
    }

    @Test
    void shouldAggregateForecastsFromMultipleLocations() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var firstLocation = new RawLocation("Tarifa", "ES");
        var secondLocation = new RawLocation("Jastarnia", "PL");

        var firstLocationForecast = new Forecast(firstLocation, requestedDate, requestedDate, 8.5, 22.0);
        var secondLocationForecast = new Forecast(secondLocation, requestedDate, requestedDate, 7.0, 18.0);

        when(apiClient.getLongtermForecastFor(firstLocation, requestedDate)).thenReturn(List.of(firstLocationForecast));
        when(apiClient.getLongtermForecastFor(secondLocation, requestedDate)).thenReturn(List.of(secondLocationForecast));

        var result = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate);

        assertThat(result).containsExactly(firstLocationForecast, secondLocationForecast);
    }

    @Test
    void shouldAggregateAndReturnOnlyForecastsForRequestedDateForMultipleLocations() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var firstLocation = new RawLocation("Tarifa", "ES");
        var secondLocation = new RawLocation("Jastarnia", "PL");

        var firstLocationForecastOnRequestedDay = new Forecast(firstLocation, requestedDate,
                requestedDate, 8.5, 22.0);
        var firstLocationForecastOnOtherDay = new Forecast(firstLocation, requestedDate,
                requestedDate.plusDays(1), 8.5, 22.0);
        var secondLocationForecastOnRequestedDay = new Forecast(secondLocation, requestedDate,
                requestedDate, 7.0, 18.0);
        var secondLocationForecastOnOtherDay = new Forecast(secondLocation, requestedDate,
                requestedDate.plusDays(1), 8.5, 22.0);

        when(apiClient.getLongtermForecastFor(firstLocation, requestedDate))
                .thenReturn(List.of(firstLocationForecastOnRequestedDay, firstLocationForecastOnOtherDay));
        when(apiClient.getLongtermForecastFor(secondLocation, requestedDate))
                .thenReturn(List.of(secondLocationForecastOnRequestedDay, secondLocationForecastOnOtherDay));

        var result = provider.provideForecastsFor(List.of(firstLocation, secondLocation), requestedDate);

        assertThat(result).containsExactly(firstLocationForecastOnRequestedDay, secondLocationForecastOnRequestedDay);
    }

    @Test
    void shouldCallApiClientForEachLocation() {
        var requestedDate = LocalDate.of(2026, 5, 10);

        var location1 = new RawLocation("Tarifa", "ES");
        var location2 = new RawLocation("Jastarnia", "PL");

        when(apiClient.getLongtermForecastFor(location1, requestedDate)).thenReturn(List.of());
        when(apiClient.getLongtermForecastFor(location2, requestedDate)).thenReturn(List.of());

        provider.provideForecastsFor(List.of(location1, location2), requestedDate);

        verify(apiClient).getLongtermForecastFor(location1, requestedDate);
        verify(apiClient).getLongtermForecastFor(location2, requestedDate);
    }
}
