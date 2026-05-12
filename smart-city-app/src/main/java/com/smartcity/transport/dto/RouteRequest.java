package com.smartcity.transport.dto;

import lombok.Data;

@Data
public class RouteRequest {
    private double originLat;
    private double originLon;
    private double destinationLat;
    private double destinationLon;
}
