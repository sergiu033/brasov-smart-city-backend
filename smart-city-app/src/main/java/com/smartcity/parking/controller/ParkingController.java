package com.smartcity.parking.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @GetMapping("/zones")
    public ApiResponse<List<Map<String, String>>> zones() {
        return ApiResponse.success(List.of(
                Map.of("zone", "A", "tariffPerHour", "5 RON", "tariffPerDay", "30 RON"),
                Map.of("zone", "B", "tariffPerHour", "3 RON", "tariffPerDay", "20 RON")),
                "Zone de parcare.");
    }

    @GetMapping("/zones/{zoneCode}")
    public ApiResponse<Map<String, String>> zoneDetails(@PathVariable String zoneCode) {
        return ApiResponse.success(Map.of(
                "zone", zoneCode,
                "tariffPerHour", "5 RON",
                "tariffPerDay", "30 RON",
                "status", "active"),
                "Detalii zona de parcare.");
    }

    @GetMapping("/tariffs")
    public ApiResponse<List<Map<String, String>>> tariffs() {
        return ApiResponse.success(List.of(
                Map.of("zone", "A", "hour", "5 RON", "day", "30 RON"),
                Map.of("zone", "B", "hour", "3 RON", "day", "20 RON")),
                "Tarife disponibile.");
    }
}
