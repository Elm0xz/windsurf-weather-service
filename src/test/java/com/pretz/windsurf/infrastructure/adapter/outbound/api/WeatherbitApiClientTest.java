package com.pretz.windsurf.infrastructure.adapter.outbound.api;


import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.pretz.windsurf.application.domain.model.RawLocation;
import com.pretz.windsurf.infrastructure.adapter.outbound.exception.WeatherApiClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class WeatherbitApiClientTest {

    //TODO remove this
    private static final String apiKey = "2f2da76fe6674f6293a9ff2f04981556";
    private static final int forecastDays = 7;
    private static final String forecastPath = "/v2.0/forecast/daily";
    private final WeatherbitApiValidator validator = mock(WeatherbitApiValidator.class);

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .build();

    @Test
    void shouldFetchAndMapForecastsFromWeatherbitApi() {
        wireMock.stubFor(get(urlPathEqualTo("/v2.0/forecast/daily"))
                .withQueryParam("key", equalTo(apiKey))
                .withQueryParam("days", equalTo("7"))
                .withQueryParam("city", equalTo("Tarifa"))
                .withQueryParam("country", equalTo("ES"))
                .willReturn(okJson(responseBody())));

        var restClient = RestClient.builder()
                .baseUrl(wireMock.baseUrl())
                .build();


        var client = new WeatherbitApiClient(restClient, apiKey, forecastDays, forecastPath, validator);

        var location = new RawLocation("Tarifa", "ES");
        var requestDate = LocalDate.of(2026, 5, 9);

        var result = client.getLongtermForecastFor(location, requestDate);

        assertThat(result).hasSize(2);

        assertThat(result.getFirst().location()).isEqualTo(location);
        assertThat(result.getFirst().requestDate()).isEqualTo(requestDate);
        assertThat(result.getFirst().forecastDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(result.getFirst().windSpeed()).isEqualTo(8.5);
        assertThat(result.getFirst().temperature()).isEqualTo(22.0);

        assertThat(result.get(1).location()).isEqualTo(location);
        assertThat(result.get(1).requestDate()).isEqualTo(requestDate);
        assertThat(result.get(1).forecastDate()).isEqualTo(LocalDate.of(2026, 5, 11));
        assertThat(result.get(1).windSpeed()).isEqualTo(6.2);
        assertThat(result.get(1).temperature()).isEqualTo(21.5);

        wireMock.verify(getRequestedFor(urlPathEqualTo("/v2.0/forecast/daily"))
                .withQueryParam("key", equalTo(apiKey))
                .withQueryParam("days", equalTo("7"))
                .withQueryParam("city", equalTo("Tarifa"))
                .withQueryParam("country", equalTo("ES")));
    }

    private static String responseBody() {
        return """
                {
                  "data": [
                    {
                      "valid_date": "2026-05-10",
                      "wind_spd": 8.5,
                      "temp": 22.0
                    },
                    {
                      "valid_date": "2026-05-11",
                      "wind_spd": 6.2,
                      "temp": 21.5
                    }
                  ]
                }
                """;
    }

    @Test
    void shouldThrowExceptionWhenWeatherbitReturnsServerError() {
        wireMock.stubFor(get(urlPathEqualTo("/v2.0/forecast/daily"))
                .willReturn(serverError()));

        var restClient = RestClient.builder()
                .baseUrl(wireMock.baseUrl())
                .build();

        var client = new WeatherbitApiClient(restClient, apiKey, forecastDays, forecastPath, validator);

        var location = new RawLocation("Tarifa", "ES");
        var requestDate = LocalDate.of(2026, 5, 9);

        assertThatThrownBy(() -> client.getLongtermForecastFor(location, requestDate))
                .isInstanceOf(WeatherApiClientException.class)
                .hasMessage("Could not fetch long-term forecast from Weatherbit");
    }
}
