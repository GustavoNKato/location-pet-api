package com.itau.challenge_localization_api.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing location information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String country;
    private String state;
    private String city;
    private String neighborhood;
    private String address;
}
