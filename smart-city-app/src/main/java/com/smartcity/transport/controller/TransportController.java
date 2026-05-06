package com.smartcity.transport.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    @GetMapping("/lines")
    public ResponseEntity<List<Map<String, String>>> lines() {
        return ResponseEntity.ok(List.of(
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

    @GetMapping("/route-search")
    public ResponseEntity<Map<String, Object>> routeSearch(
            @RequestParam String origin,
            @RequestParam String destination) {
        return ResponseEntity.ok(Map.of(
                "origin", origin,
                "destination", destination,
                "recommendedLine", "22",
                "estimatedTravelMinutes", 28,
                "mapProvider", "google-maps"));
    }

    @GetMapping("/warnings/today")
    public ResponseEntity<List<Map<String, String>>> warnings(@RequestParam(required = false) String date) {
        return ResponseEntity.ok(List.of(
                Map.of("type", "holiday", "message", "Program redus in zilele de sarbatoare.", "date",
                        date == null ? "today" : date)));
    }
}
