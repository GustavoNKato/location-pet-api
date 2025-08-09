package com.itau.challenge_location_api.infrastructure.client;

import com.itau.challenge_location_api.infrastructure.client.model.PositionStackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for PositionStack API.
 * This defines the contract for communication with the external service.
 */
@FeignClient(name = "positionstack", url = "${position-stack.base-url}")
public interface PositionStackClient {
    
    /**
     * Performs reverse geocoding (converts coordinates to address)
     * 
     * @param accessKey API key for authentication
     * @param query coordinates in format "latitude,longitude"
     * @return response containing location details
     */
    @GetMapping("/reverse")
    PositionStackResponse reverseGeocode(
            @RequestParam("access_key") String accessKey,
            @RequestParam("query") String query
    );
}
