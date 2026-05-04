package com.smartcity.recommendations.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationsController {

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, String>>> categories() {
        return ApiResponse.success(List.of(
                Map.of("code", "gastronomie", "label", "Gastronomie"),
                Map.of("code", "natura", "label", "Natura"),
                Map.of("code", "plimbare", "label", "Plimbare in oras"),
                Map.of("code", "cultura", "label", "Cultura"),
                Map.of("code", "experiente", "label", "Experiente")),
                "Categorii disponibile.");
    }

    @GetMapping
    public ApiResponse<List<Map<String, String>>> recommendations(@RequestParam(defaultValue = "general") String category) {
        return ApiResponse.success(List.of(
                Map.of("category", category, "title", "Tur pietonal centru vechi", "location", "Piata Sfatului"),
                Map.of("category", category, "title", "Cafea cu vedere spre Tampa", "location", "Aleea de sub Tampa")),
                "Recomandari disponibile.");
    }

    @GetMapping("/{recommendationId}")
    public ApiResponse<Map<String, String>> recommendationDetails(@PathVariable String recommendationId) {
        return ApiResponse.success(Map.of(
                "id", recommendationId,
                "title", "Recomandare selectata",
                "location", "Brasov",
                "description", "Detalii demonstrative pentru cardul de recomandare."),
                "Detalii recomandare.");
    }
}
