package com.smartcity.transport.controller;

import com.smartcity.transport.dto.RouteRequest;
import com.smartcity.transport.dto.RouteResponse;
import com.smartcity.transport.service.TransportService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;

    @GetMapping("/lines")
    public ResponseEntity<List<Map<String, String>>> lines() {
        return ResponseEntity.ok(List.of(
                Map.of("line", "4", "destination", "Tocile / Gara Brasov", "status", "active"),
                Map.of("line", "22", "destination", "Poiana Brasov", "status", "active"),
                Map.of("line", "17", "destination", "Gara Brasov", "status", "active")));
    }

    @GetMapping("/lines/{lineCode}")
    public ResponseEntity<Map<String, Object>> lineDetails(@PathVariable String lineCode) {
        return ResponseEntity.ok(Map.of(
                "line", lineCode,
                "destination", "Poiana Brasov",
                "scheduleType", "workday",
                "stops", List.of("Gara Brasov", "Livada Postei", "Poiana Brasov")));
    }

    @PostMapping("/route-search")
    public ResponseEntity<RouteResponse> routeSearch(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(transportService.findRoute(request));
    }

    @GetMapping("/warnings/today")
    public ResponseEntity<List<Map<String, String>>> warnings(@RequestParam(required = false) String date) {
        return ResponseEntity.ok(List.of(
                Map.of("type", "holiday", "message", "Program redus in zilele de sarbatoare.", "date",
                        date == null ? "today" : date)));
    }
}
