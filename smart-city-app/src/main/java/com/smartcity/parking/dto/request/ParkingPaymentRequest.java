package com.smartcity.parking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
public record ParkingPaymentRequest (
        @NotNull
        Long vehicleId,
        @NotBlank(message = "Zone code is required")
        String zoneCode,
        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 hour")
        Integer durationHours,
        Double latitude,
        Double longitude
){}
