package com.itau.challenge_location_api.application.service;

import com.itau.challenge_location_api.domain.model.LocationInfo;
import com.itau.challenge_location_api.domain.model.PetSensorData;
import com.itau.challenge_location_api.domain.service.LocationService;
import com.itau.challenge_location_api.infrastructure.client.PositionStackClient;
import com.itau.challenge_location_api.infrastructure.client.model.PositionStackData;
import com.itau.challenge_location_api.infrastructure.client.model.PositionStackResponse;
import com.itau.challenge_location_api.infrastructure.config.PositionStackConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementation of the LocationService interface that uses the PositionStack API
 * to convert coordinates to address information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final PositionStackClient positionStackClient;
    private final PositionStackConfig positionStackConfig;

    @Override
    public LocationInfo getLocationFromCoordinates(PetSensorData sensorData) {
        log.info("Getting location for pet sensor: {} at coordinates ({}, {})",
                sensorData.getSensorId(), sensorData.getLatitude(), sensorData.getLongitude());
        try {
            String coordinates = String.format(Locale.US, "%f,%f", sensorData.getLatitude(), sensorData.getLongitude());
            log.debug("Formatted coordinates for API call: {}", coordinates);
            PositionStackResponse response = positionStackClient.reverseGeocode(
                    positionStackConfig.getKey(), 
                    coordinates
            );
            return mapToLocationInfo(response, sensorData);
        } catch (Exception e) {
            log.error("Error getting location from PositionStack API", e);
            throw new RuntimeException("Failed to retrieve location information", e);
        }
    }
    
    /**
     * Maps PositionStack API response to our domain LocationInfo model.
     * 
     * @param response API response from PositionStack
     * @param sensorData original sensor data
     * @return mapped location information
     */
    private LocationInfo mapToLocationInfo(PositionStackResponse response, PetSensorData sensorData) {
        List<PositionStackData> dataList = Optional.ofNullable(response.getData())
                .orElse(Collections.emptyList());
        if (dataList.isEmpty()) {
            log.warn("No location data found for coordinates: {}, {}", 
                    sensorData.getLatitude(), sensorData.getLongitude());
            return createEmptyLocationInfo(sensorData);
        }
        // Use the first result (most relevant)
        PositionStackData data = dataList.get(0);
        log.debug("Mapped location data: {}", data);
        String neighborhood = data.getNeighborhood();
        if (neighborhood == null && data.getAdministrativeArea() != null) {
            neighborhood = data.getAdministrativeArea();
            log.debug("Using administrativeArea as neighborhood: {}", neighborhood);
        }
        return LocationInfo.builder()
                .sensorId(sensorData.getSensorId())
                .latitude(sensorData.getLatitude())
                .longitude(sensorData.getLongitude())
                .country(data.getCountry())
                .state(data.getRegion())
                .city(data.getLocality())
                .neighborhood(neighborhood)
                .street(data.getStreet())
                .number(data.getNumber())
                .postalCode(data.getPostalCode())
                .label(data.getLabel())
                .build();
    }
    
    /**
     * Creates an empty LocationInfo when no data is available.
     * 
     * @param sensorData original sensor data
     * @return location info with only sensor data
     */
    private LocationInfo createEmptyLocationInfo(PetSensorData sensorData) {
        return LocationInfo.builder()
                .sensorId(sensorData.getSensorId())
                .latitude(sensorData.getLatitude())
                .longitude(sensorData.getLongitude())
                .build();
    }
}
