package com.itau.challenge_location_api.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for pet location data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetLocationRequest {
    @NotBlank(message = "Sensor ID is required")
    private String sensorId;
    @NotNull(message = "Latitude is required")
    private Double latitude;
    @NotNull(message = "Longitude is required")
    private Double longitude;
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}
