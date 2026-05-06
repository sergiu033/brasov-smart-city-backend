package com.smartcity.recommendation.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationsController {

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> categories() {
        return ResponseEntity.ok(List.of(
                Map.of("code", "gastronomie", "label", "Gastronomie"),
                Map.of("code", "natura", "label", "Natura"),
                Map.of("code", "plimbare", "label", "Plimbare in oras"),
                Map.of("code", "cultura", "label", "Cultura"),
                Map.of("code", "experiente", "label", "Experiente")));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> recommendations(@RequestParam(defaultValue = "general") String category) {
        return ResponseEntity.ok(List.of(
                Map.of("category", category, "title", "Tur pietonal centru vechi", "location", "Piata Sfatului"),
                Map.of("category", category, "title", "Cafea cu vedere spre Tampa", "location", "Aleea de sub Tampa")));
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<Map<String, String>> recommendationDetails(@PathVariable String recommendationId) {
        return ResponseEntity.ok(Map.of(
                "id", recommendationId,
                "title", "Recomandare selectata",
                "location", "Brasov",
                "description", "Detalii demonstrative pentru cardul de recomandare."));
    }
}
