package com.smartcity.events.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    @GetMapping("/current")
    public ApiResponse<List<Map<String, String>>> current() {
        return ApiResponse.success(List.of(
                Map.of("title", "Concert in parc", "when", "saptamana curenta", "location", "Parcul Nicolae Titulescu")),
                "Evenimente curente.");
    }

    @GetMapping("/next-week")
    public ApiResponse<List<Map<String, String>>> nextWeek() {
        return ApiResponse.success(List.of(
                Map.of("title", "Festival local", "when", "saptamana urmatoare", "location", "Piata Sfatului")),
                "Evenimente pentru saptamana urmatoare.");
    }

    @GetMapping("/{eventId}")
    public ApiResponse<Map<String, String>> eventDetails(@PathVariable String eventId) {
        return ApiResponse.success(Map.of(
                "id", eventId,
                "title", "Eveniment selectat",
                "location", "Brasov",
                "time", "18:00",
                "status", "planned"),
                "Detalii eveniment.");
    }
}
