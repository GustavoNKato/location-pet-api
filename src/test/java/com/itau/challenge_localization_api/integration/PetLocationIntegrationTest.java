package com.itau.challenge_localization_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.challenge_localization_api.infrastructure.client.PositionStackClient;
import com.itau.challenge_localization_api.infrastructure.client.model.PositionStackData;
import com.itau.challenge_localization_api.infrastructure.client.model.PositionStackResponse;
import com.itau.challenge_localization_api.presentation.dto.request.PetLocationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the Pet Location API.
 * This test verifies the full flow from controller to service using a mocked external API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PetLocationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PositionStackClient positionStackClient;
    
    private ObjectMapper objectMapper;
    private PetLocationRequest validRequest;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Set up test data
        validRequest = PetLocationRequest.builder()
                .sensorId("test-sensor-123")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .timestamp(LocalDateTime.now())
                .build();
        
        // Mock the PositionStack API response
        PositionStackData positionData = new PositionStackData();
        positionData.setLatitude(validRequest.getLatitude());
        positionData.setLongitude(validRequest.getLongitude());
        positionData.setCountry("Brazil");
        positionData.setRegion("São Paulo");
        positionData.setLocality("São Paulo");
        positionData.setNeighborhood("Centro");
        positionData.setStreet("Avenida Paulista");
        positionData.setNumber("123");
        positionData.setPostalCode("01310-000");
        positionData.setLabel("Avenida Paulista, 123, São Paulo, Brazil");
        
        PositionStackResponse mockResponse = new PositionStackResponse();
        mockResponse.setData(List.of(positionData));
        
        // Configure mock to return our test data
        when(positionStackClient.reverseGeocode(anyString(), anyString()))
                .thenReturn(mockResponse);
    }
    
    @Test
    void shouldReturnLocationInfo_whenValidRequestIsSent() throws Exception {
        mockMvc.perform(post("/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Brazil"))
                .andExpect(jsonPath("$.state").value("São Paulo"))
                .andExpect(jsonPath("$.city").value("São Paulo"))
                .andExpect(jsonPath("$.neighborhood").value("Centro"))
                .andExpect(jsonPath("$.address").value("Avenida Paulista, 123, São Paulo, Brazil"));
    }
    
    @Test
    void shouldReturnBadRequest_whenInvalidRequestIsSent() throws Exception {
        // Create an invalid request with missing required fields
        PetLocationRequest invalidRequest = new PetLocationRequest();
        invalidRequest.setSensorId(null);
        
        mockMvc.perform(post("/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
