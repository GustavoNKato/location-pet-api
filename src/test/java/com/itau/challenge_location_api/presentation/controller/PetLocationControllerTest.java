package com.itau.challenge_location_api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.challenge_location_api.domain.model.LocationInfo;
import com.itau.challenge_location_api.domain.model.PetSensorData;
import com.itau.challenge_location_api.domain.service.LocationService;
import com.itau.challenge_location_api.infrastructure.metrics.LocationMetricsService;
import com.itau.challenge_location_api.presentation.dto.request.PetLocationRequest;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PetLocationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LocationService locationService;

    @Mock
    private LocationMetricsService metricsService;

    @InjectMocks
    private PetLocationController petLocationController;

    private ObjectMapper objectMapper;
    private PetLocationRequest validRequest;
    private LocationInfo mockLocationInfo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petLocationController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create a valid request
        validRequest = PetLocationRequest.builder()
                .sensorId("test-sensor-123")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .timestamp(LocalDateTime.now())
                .build();

        // Mock the service response
        mockLocationInfo = LocationInfo.builder()
                .sensorId(validRequest.getSensorId())
                .latitude(validRequest.getLatitude())
                .longitude(validRequest.getLongitude())
                .country("Brazil")
                .state("São Paulo")
                .city("São Paulo")
                .neighborhood("Centro")
                .street("Avenida Paulista")
                .number("123")
                .label("Avenida Paulista, 123, São Paulo, Brazil")
                .build();
    }

    @Test
    void getLocationFromSensorData_ShouldReturnLocationInfo_WhenRequestIsValid() throws Exception {
        // Arrange
        Timer.Sample mockTimerSample = mock(Timer.Sample.class);
        when(metricsService.startLocationRequestTimer()).thenReturn(mockTimerSample);
        when(locationService.getLocationFromCoordinates(any(PetSensorData.class)))
                .thenReturn(mockLocationInfo);

        // Act & Assert
        mockMvc.perform(post("/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value(mockLocationInfo.getCountry()))
                .andExpect(jsonPath("$.state").value(mockLocationInfo.getState()))
                .andExpect(jsonPath("$.city").value(mockLocationInfo.getCity()))
                .andExpect(jsonPath("$.neighborhood").value(mockLocationInfo.getNeighborhood()))
                .andExpect(jsonPath("$.address").value(mockLocationInfo.getLabel()));
    }

    @Test
    void getLocationFromSensorData_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        // Arrange - Create an invalid request with null required fields
        PetLocationRequest invalidRequest = new PetLocationRequest();
        invalidRequest.setSensorId(null); // Missing required field

        // Act & Assert
        mockMvc.perform(post("/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
