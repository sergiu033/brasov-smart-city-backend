package com.smartcity.parking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartcity.parking.enums.ParkingZoneStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ParkingZoneResponse(
        @JsonProperty("zone")
        String zoneCode,
        BigDecimal tariffPerHour,
        BigDecimal tariffPerDay
) {
}
