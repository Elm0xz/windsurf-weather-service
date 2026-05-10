package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.infrastructure.adapter.outbound.api.WeatherApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "windsurf.locations.source-name=missing-locations.json"
})
@AutoConfigureMockMvc
class WindsurfWeatherMissingLocationsIntegrationTest {

    private static final String ENDPOINT = "/api/windsurfing-location";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherApiClient weatherApiClient;

    @Test
    void shouldReturnInternalServerErrorWhenLocationsFileCannotBeLoaded() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .param("date", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Locations source is currently unavailable"));
    }
}
