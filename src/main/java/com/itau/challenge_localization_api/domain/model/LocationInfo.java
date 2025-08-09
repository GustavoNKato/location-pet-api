package com.itau.challenge_localization_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing the location information of a pet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationInfo {
    private String country;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String number;
    private String postalCode;
    private String label;
    private String sensorId;
    private double latitude;
    private double longitude;
}
