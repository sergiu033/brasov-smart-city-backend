package com.smartcity.user_vehicles.dto;

import jakarta.validation.constraints.NotBlank;

public record VehicleRequest(
        @NotBlank
        String plateNumber
) {
}
