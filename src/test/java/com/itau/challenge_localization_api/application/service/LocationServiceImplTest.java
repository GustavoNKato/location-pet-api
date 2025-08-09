package com.itau.challenge_localization_api.application.service;

import com.itau.challenge_localization_api.domain.model.LocationInfo;
import com.itau.challenge_localization_api.domain.model.PetSensorData;
import com.itau.challenge_localization_api.infrastructure.client.PositionStackClient;
import com.itau.challenge_localization_api.infrastructure.client.model.PositionStackData;
import com.itau.challenge_localization_api.infrastructure.client.model.PositionStackResponse;
import com.itau.challenge_localization_api.infrastructure.config.PositionStackConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private PositionStackClient positionStackClient;

    @Mock
    private PositionStackConfig positionStackConfig;

    @InjectMocks
    private LocationServiceImpl locationService;

    private PetSensorData sensorData;
    private PositionStackResponse positionStackResponse;
    private PositionStackData positionStackData;
    private static final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        // Set up test data
        sensorData = PetSensorData.builder()
                .sensorId("test-sensor-123")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .timestamp(LocalDateTime.now())
                .build();

        // Mock PositionStack API data
        positionStackData = new PositionStackData();
        positionStackData.setLatitude(-23.5505);
        positionStackData.setLongitude(-46.6333);
        positionStackData.setCountry("Brazil");
        positionStackData.setRegion("São Paulo");
        positionStackData.setLocality("São Paulo");
        positionStackData.setNeighborhood("Centro");
        positionStackData.setStreet("Avenida Paulista");
        positionStackData.setNumber("123");
        positionStackData.setPostalCode("01310-000");
        positionStackData.setLabel("Avenida Paulista, 123, São Paulo, Brazil");

        positionStackResponse = new PositionStackResponse();
        positionStackResponse.setData(List.of(positionStackData));

        // Mock config
        when(positionStackConfig.getKey()).thenReturn(API_KEY);
    }

    @Test
    void getLocationFromCoordinates_ShouldReturnLocationInfo_WhenApiReturnsData() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenReturn(positionStackResponse);

        // Act
        LocationInfo result = locationService.getLocationFromCoordinates(sensorData);

        // Assert
        assertNotNull(result);
        assertEquals(sensorData.getSensorId(), result.getSensorId());
        assertEquals(sensorData.getLatitude(), result.getLatitude());
        assertEquals(sensorData.getLongitude(), result.getLongitude());
        assertEquals(positionStackData.getCountry(), result.getCountry());
        assertEquals(positionStackData.getRegion(), result.getState());
        assertEquals(positionStackData.getLocality(), result.getCity());
        assertEquals(positionStackData.getNeighborhood(), result.getNeighborhood());
        assertEquals(positionStackData.getStreet(), result.getStreet());
        assertEquals(positionStackData.getNumber(), result.getNumber());
        assertEquals(positionStackData.getPostalCode(), result.getPostalCode());
        assertEquals(positionStackData.getLabel(), result.getLabel());

        // Verify interactions
        verify(positionStackClient).reverseGeocode(eq(API_KEY), eq(coordinates));
        verify(positionStackConfig).getKey();
    }

    @Test
    void getLocationFromCoordinates_ShouldReturnEmptyLocationInfo_WhenApiReturnsNoData() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        
        PositionStackResponse emptyResponse = new PositionStackResponse();
        emptyResponse.setData(Collections.emptyList());
        
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenReturn(emptyResponse);

        // Act
        LocationInfo result = locationService.getLocationFromCoordinates(sensorData);

        // Assert
        assertNotNull(result);
        assertEquals(sensorData.getSensorId(), result.getSensorId());
        assertEquals(sensorData.getLatitude(), result.getLatitude());
        assertEquals(sensorData.getLongitude(), result.getLongitude());
        assertNull(result.getCountry());
        assertNull(result.getState());
        assertNull(result.getCity());
        assertNull(result.getNeighborhood());
        assertNull(result.getStreet());

        // Verify interactions
        verify(positionStackClient).reverseGeocode(eq(API_KEY), eq(coordinates));
        verify(positionStackConfig).getKey();
    }

    @Test
    void getLocationFromCoordinates_ShouldThrowException_WhenApiCallFails() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> locationService.getLocationFromCoordinates(sensorData));

        // Verify interactions
        verify(positionStackClient).reverseGeocode(eq(API_KEY), eq(coordinates));
        verify(positionStackConfig).getKey();
    }

    @Test
    void getLocationFromCoordinates_ShouldHandleNullDataFromApi() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        
        PositionStackResponse nullDataResponse = new PositionStackResponse();
        nullDataResponse.setData(null); // API returns null data list
        
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenReturn(nullDataResponse);

        // Act
        LocationInfo result = locationService.getLocationFromCoordinates(sensorData);

        // Assert
        assertNotNull(result);
        assertEquals(sensorData.getSensorId(), result.getSensorId());
        assertEquals(sensorData.getLatitude(), result.getLatitude());
        assertEquals(sensorData.getLongitude(), result.getLongitude());
        assertNull(result.getCountry());
        assertNull(result.getState());
        assertNull(result.getCity());
        assertNull(result.getNeighborhood());
        assertNull(result.getStreet());
    }

    @Test
    void getLocationFromCoordinates_ShouldHandleNullLabelFromApi() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        
        PositionStackData positionStackData = new PositionStackData();
        positionStackData.setLatitude(-23.5505);
        positionStackData.setLongitude(-46.6333);
        positionStackData.setCountry("Brazil");
        positionStackData.setRegion("São Paulo");
        positionStackData.setLocality("São Paulo");
        positionStackData.setNeighborhood("Centro");
        positionStackData.setStreet("Avenida Paulista");
        positionStackData.setNumber("123");
        positionStackData.setPostalCode("01310-000");
        positionStackData.setLabel(null);

        PositionStackResponse positionStackResponse = new PositionStackResponse();
        positionStackResponse.setData(List.of(positionStackData));
        
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenReturn(positionStackResponse);

        // Act
        LocationInfo result = locationService.getLocationFromCoordinates(sensorData);

        // Assert
        assertNotNull(result);
        assertEquals(sensorData.getSensorId(), result.getSensorId());
        assertEquals(sensorData.getLatitude(), result.getLatitude());
        assertEquals(sensorData.getLongitude(), result.getLongitude());
        assertEquals(positionStackData.getCountry(), result.getCountry());
        assertEquals(positionStackData.getRegion(), result.getState());
        assertEquals(positionStackData.getLocality(), result.getCity());
        assertEquals(positionStackData.getNeighborhood(), result.getNeighborhood());
        assertEquals(positionStackData.getStreet(), result.getStreet());
        assertEquals(positionStackData.getNumber(), result.getNumber());
        assertEquals(positionStackData.getPostalCode(), result.getPostalCode());
        assertNull(result.getLabel());
    }

    @Test
    void getLocationFromCoordinates_ShouldUseAdministrativeAreaAsNeighborhood_WhenNeighborhoodIsNull() {
        // Arrange
        String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
        
        PositionStackData positionStackData = new PositionStackData();
        positionStackData.setLatitude(-23.5505);
        positionStackData.setLongitude(-46.6333);
        positionStackData.setCountry("Brazil");
        positionStackData.setRegion("São Paulo");
        positionStackData.setLocality("São Paulo");
        positionStackData.setNeighborhood(null); // null neighborhood
        positionStackData.setAdministrativeArea("Jardim Paulista"); // administrativeArea available
        positionStackData.setStreet("Avenida Paulista");
        positionStackData.setNumber("2240");
        positionStackData.setPostalCode("01310-300");
        positionStackData.setLabel("Avenida Paulista, 2240, São Paulo, Brazil");

        PositionStackResponse positionStackResponse = new PositionStackResponse();
        positionStackResponse.setData(List.of(positionStackData));
        
        when(positionStackClient.reverseGeocode(eq(API_KEY), eq(coordinates)))
                .thenReturn(positionStackResponse);

        // Act
        LocationInfo result = locationService.getLocationFromCoordinates(sensorData);

        // Assert
        assertNotNull(result);
        assertEquals(sensorData.getSensorId(), result.getSensorId());
        assertEquals(sensorData.getLatitude(), result.getLatitude());
        assertEquals(sensorData.getLongitude(), result.getLongitude());
        assertEquals(positionStackData.getCountry(), result.getCountry());
        assertEquals(positionStackData.getRegion(), result.getState());
        assertEquals(positionStackData.getLocality(), result.getCity());
        assertEquals("Jardim Paulista", result.getNeighborhood()); // Should use administrativeArea
        assertEquals(positionStackData.getStreet(), result.getStreet());
        assertEquals(positionStackData.getNumber(), result.getNumber());
        assertEquals(positionStackData.getPostalCode(), result.getPostalCode());
        assertEquals(positionStackData.getLabel(), result.getLabel());

        // Verify interactions
        verify(positionStackClient).reverseGeocode(eq(API_KEY), eq(coordinates));
        verify(positionStackConfig).getKey();
    }
}
