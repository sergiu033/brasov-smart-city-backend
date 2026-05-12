package com.smartcity.parking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ParkingPaymentResponse {
    private Long id;
    private String plateNumber;
    private String zoneCode;
    private Integer durationHours;
    private BigDecimal totalPrice;
    private Boolean isPaid;
    private LocalDateTime paymentDate;
    private String status;
}
