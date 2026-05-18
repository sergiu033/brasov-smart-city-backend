package com.smartcity.user_vehicles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VehicleRequest(
        @NotBlank(message = "Numărul de înmatriculare este obligatoriu.")
        @Pattern(regexp = "^[A-Z0-9- ]{2,20}$", message = "Numărul de înmatriculare nu este valid.")
        String plateNumber
) {
}
