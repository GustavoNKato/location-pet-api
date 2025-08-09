package com.itau.challenge_localization_api.infrastructure.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Model representing a single location data entry from PositionStack API.
 * The field names match the API response structure from PositionStack.
 */
@Data
public class PositionStackData {
    private Double latitude;
    private Double longitude;
    private String name;
    private String country;
    private String region;
    
    @JsonProperty("region_code")
    private String regionCode;
    
    private String county;
    private String locality;
    private String neighborhood;
    
    @JsonProperty("street") // será que preciso dessa anotação?
    private String street;
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("postal_code")
    private String postalCode;
    
    private String label;
    
    @JsonProperty("continent")
    private String continent;
    
    @JsonProperty("administrative_area")
    private String administrativeArea;
    
    private Integer confidence;
}
