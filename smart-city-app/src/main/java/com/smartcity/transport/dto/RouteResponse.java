package com.smartcity.transport.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteResponse {
    private int durationMinutes;
    private List<RouteLeg> legs;
    private double totalDistance;
}
