package com.smartcity.parking.dto.request;

import com.smartcity.parking.enums.ParkingZoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ParkingZoneUpdateRequest(
        @NotNull(message = "Id-ul zonei de parcare nu poate fi gol")
        Long id,
        @NotBlank(message = "Codul zonei de parcare nu poate fi gol")
        String zoneCode,
        @Positive(message = "Tariful pe ora nu poate fi mai mic sau egal cu 0")
        @NotNull(message = "Tariful pe ora nu poate fi gol.")
        BigDecimal tariffPerHour,
        @NotNull(message = "Tariful pe zi nu poate fi gol.")
        @Positive(message = "Tariful pe zi nu poate fi mai mic sau egal cu 0")
        BigDecimal tariffPerDay,
        ParkingZoneStatus status
) {
}
