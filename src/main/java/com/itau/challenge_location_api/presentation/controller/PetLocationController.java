package com.itau.challenge_location_api.presentation.controller;

import com.itau.challenge_location_api.domain.model.LocationInfo;
import com.itau.challenge_location_api.domain.model.PetSensorData;
import com.itau.challenge_location_api.domain.service.LocationService;
import com.itau.challenge_location_api.infrastructure.metrics.LocationMetricsService;
import com.itau.challenge_location_api.presentation.dto.request.PetLocationRequest;
import com.itau.challenge_location_api.presentation.dto.response.LocationResponse;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for pet location operations.
 * Handles HTTP requests for retrieving location information from pet sensor data.
 */
@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
@Slf4j
public class PetLocationController {

    private final LocationService locationService;
    private final LocationMetricsService metricsService;

    /**
     * Retrieves location information from pet sensor data.
     *
     * @param request the pet location request containing sensor data
     * @return location information including country, state, city, neighborhood, and address
     */
    @PostMapping
    public ResponseEntity<LocationResponse> getLocationFromSensorData(@Valid @RequestBody PetLocationRequest request) {
        Timer.Sample requestTimer = metricsService.startLocationRequestTimer();
        metricsService.incrementLocationRequests();
        
        try {
            log.info("Received location request for sensor: {}", request.getSensorId());
            PetSensorData sensorData = PetSensorData.builder()
                    .sensorId(request.getSensorId())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .timestamp(request.getTimestamp())
                    .build();
            
            LocationInfo locationInfo = locationService.getLocationFromCoordinates(sensorData);
            LocationResponse response = mapToResponse(locationInfo);
            
            metricsService.incrementLocationRequestsSuccess();
            log.info("Location found for sensor: {}", request.getSensorId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            metricsService.incrementLocationRequestsError("processing_error");
            log.error("Error processing location request for sensor: {}", request.getSensorId(), e);
            throw e;
        } finally {
            metricsService.stopTimer(requestTimer);
        }
    }

    /**
     * Maps LocationInfo domain model to LocationResponse DTO.
     *
     * @param locationInfo the domain model
     * @return the response DTO
     */
    private LocationResponse mapToResponse(LocationInfo locationInfo) {
        return LocationResponse.builder()
                .country(locationInfo.getCountry())
                .state(locationInfo.getState())
                .city(locationInfo.getCity())
                .neighborhood(locationInfo.getNeighborhood())
                .address(locationInfo.getLabel())
                .build();
    }
}
