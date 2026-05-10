package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.domain.model.Forecast;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherApiClient;
import com.pretz.windsurf.infrastructure.adapter.outbound.exception.WeatherApiClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "windsurf.locations.source-name=test-locations.json"
})
@AutoConfigureMockMvc
class WindsurfWeatherIntegrationTest {

    private static final String ENDPOINT = "/api/windsurfing-location";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherApiClient weatherApiClient;

    @Test
    void shouldReturnOptimalLocation() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);

        when(weatherApiClient.getLongtermForecastFor(any(RawLocation.class), eq(date)))
                .thenAnswer(invocation -> {
                    RawLocation location = invocation.getArgument(0);

                    if ("Tarifa".equals(location.name())) {
                        return List.of(new Forecast(location, date, date, 10.0, 20.0));
                    }

                    if ("Hood River".equals(location.name())) {
                        return List.of(new Forecast(location, date, date, 15.0, 25.0));
                    }

                    if ("Le Morne".equals(location.name())) {
                        return List.of(new Forecast(location, date, date, 8.0, 36.0));
                    }

                    return List.of();
                });

        mockMvc.perform(get(ENDPOINT)
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.location").value("Hood River"))
                .andExpect(jsonPath("$.windSpeed").value(15.0))
                .andExpect(jsonPath("$.temperature").value(25.0));

        verify(weatherApiClient, atLeastOnce()).getLongtermForecastFor(any(RawLocation.class), eq(date));
    }

    @Test
    void shouldReturnNotFoundWhenNoLocationHasSuitableWeather() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);

        when(weatherApiClient.getLongtermForecastFor(any(RawLocation.class), eq(date)))
                .thenAnswer(invocation -> {
                    RawLocation location = invocation.getArgument(0);

                    return List.of(new Forecast(location, date, date, 2.0, 3.0));
                });

        mockMvc.perform(get(ENDPOINT)
                        .param("date", date.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(weatherApiClient, atLeastOnce()).getLongtermForecastFor(any(RawLocation.class), eq(date));
    }

    @Test
    void shouldReturnBadRequestWhenDateIsOutsideForecastRange() throws Exception {
        LocalDate date = LocalDate.now().plusDays(8);

        mockMvc.perform(get(ENDPOINT)
                        .param("date", date.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Forecast date should be in the 7 forecast day range"));

        verify(weatherApiClient, never()).getLongtermForecastFor(any(RawLocation.class), eq(date));
    }

    @Test
    void shouldReturnBadRequestWhenDateHasInvalidFormat() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("date", "tomorrow"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Date must be provided in ISO format: yyyy-MM-dd"));
    }

    @Test
    void shouldReturnBadRequestWhenDateParameterIsMissing() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Required request parameter 'date' is missing"));

        verify(weatherApiClient, never()).getLongtermForecastFor(any(RawLocation.class), any(LocalDate.class));
    }

    @Test
    void shouldReturnBadGatewayWhenWeatherClientFails() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);

        when(weatherApiClient.getLongtermForecastFor(any(RawLocation.class), eq(date)))
                .thenThrow(new WeatherApiClientException("Could not fetch long-term forecast from Weatherbit"));

        mockMvc.perform(get(ENDPOINT)
                        .param("date", date.toString()))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Forecast provider is currently unavailable"));

        verify(weatherApiClient, atLeastOnce()).getLongtermForecastFor(any(RawLocation.class), eq(date));
    }
}
