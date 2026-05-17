package com.smartcity.parking.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ParkingPaymentResponse(
        Long id,
        String plateNumber,
        String zoneCode,
        Integer durationHours,
        BigDecimal totalPrice,
        Boolean isPaid,
        LocalDateTime paymentDate,
        String status
) {

}
