package com.smartcity.transport.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    @GetMapping("/lines")
    public ApiResponse<List<Map<String, String>>> lines() {
        return ApiResponse.success(List.of(
                Map.of("line", "22", "destination", "Poiana Brasov", "status", "active"),
                Map.of("line", "17", "destination", "Gara Brasov", "status", "active")),
                "Linii disponibile.");
    }

    @GetMapping("/lines/{lineCode}")
    public ApiResponse<Map<String, Object>> lineDetails(@PathVariable String lineCode) {
        return ApiResponse.success(Map.of(
                "line", lineCode,
                "destination", "Poiana Brasov",
                "scheduleType", "workday",
                "stops", List.of("Gara Brasov", "Livada Postei", "Poiana Brasov")),
                "Detalii pentru linia selectata.");
    }

    @GetMapping("/route-search")
    public ApiResponse<Map<String, Object>> routeSearch(
            @RequestParam String origin,
            @RequestParam String destination) {
        return ApiResponse.success(Map.of(
                "origin", origin,
                "destination", destination,
                "recommendedLine", "22",
                "estimatedTravelMinutes", 28,
                "mapProvider", "google-maps"),
                "Ruta estimata a fost calculata.");
    }

    @GetMapping("/warnings/today")
    public ApiResponse<List<Map<String, String>>> warnings(@RequestParam(required = false) String date) {
        return ApiResponse.success(List.of(
                Map.of("type", "holiday", "message", "Program redus in zilele de sarbatoare.", "date", date == null ? "today" : date)),
                "Avertismente active.");
    }
}
