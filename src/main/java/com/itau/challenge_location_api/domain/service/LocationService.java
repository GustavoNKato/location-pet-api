package com.itau.challenge_location_api.domain.service;

import com.itau.challenge_location_api.domain.model.LocationInfo;
import com.itau.challenge_location_api.domain.model.PetSensorData;

/**
 * Service interface for geocoding operations in the domain layer.
 * Following the Dependency Inversion principle, this interface defines
 * the contract that any location service implementation must fulfill.
 */
public interface LocationService {
    
    /**
     * Converts pet sensor data (latitude and longitude) into detailed location information.
     * 
     * @param sensorData the pet sensor data containing coordinates
     * @return detailed location information
     */
    LocationInfo getLocationFromCoordinates(PetSensorData sensorData);
}
