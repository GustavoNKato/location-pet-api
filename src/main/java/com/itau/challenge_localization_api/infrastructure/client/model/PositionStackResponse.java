package com.itau.challenge_localization_api.infrastructure.client.model;

import lombok.Data;

import java.util.List;

/**
 * Model representing the response from PositionStack API.
 */
@Data
public class PositionStackResponse {
    private List<PositionStackData> data;
}
