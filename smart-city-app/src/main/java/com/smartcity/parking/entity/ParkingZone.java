package com.smartcity.parking.entity;

import com.smartcity.parking.enums.ParkingZoneStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parking_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_code", nullable = false, unique = true)
    private String zoneCode;

    @Column(name = "tariff_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal tariffPerHour;

    @Column(name = "tariff_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal tariffPerDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParkingZoneStatus status;

    @PrePersist
    private void handleDefaults() {
        if (status == null)
            this.status = ParkingZoneStatus.ACTIVE;
    }
}
