package com.smartcity.parking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingPaymentRequest {
    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotBlank(message = "Zone code is required")
    private String zoneCode;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationHours;

    private Double latitude;
    private Double longitude;
}
