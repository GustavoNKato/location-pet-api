package com.itau.challenge_localization_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model representing the data received from a pet collar sensor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetSensorData {
    private String sensorId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
