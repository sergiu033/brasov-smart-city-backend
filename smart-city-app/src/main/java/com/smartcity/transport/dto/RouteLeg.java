package com.smartcity.transport.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteLeg {
    private String mode; // WALK, BUS
    private String route; // e.g., "4"
    private String fromStop;
    private String toStop;
    private String departureTime;
    private String arrivalTime;
    private double distance;
    private int durationMinutes;
    private String instructions;
}
