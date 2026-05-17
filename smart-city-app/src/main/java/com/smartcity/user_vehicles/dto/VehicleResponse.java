package com.smartcity.user_vehicles.dto;

import lombok.Builder;

@Builder
public record VehicleResponse(
        Long id,
        String plateNumber
) {
}
