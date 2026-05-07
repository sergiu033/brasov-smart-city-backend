package com.smartcity.recommendation.controller;

import com.smartcity.recommendation.dto.request.RecommendationCreateRequest;
import com.smartcity.recommendation.dto.request.RecommendationUpdateRequest;
import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.dto.response.RecommendationResponse;
import com.smartcity.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<Page<RecommendationResponse>> getRecommendationsByCategoryCode(
            @RequestParam(defaultValue = "general") String categoryCode,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(recommendationService.findByCategory(categoryCode, pageable));
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<RecommendationDetailsResponse> getRecommendationDetails(
            @PathVariable Long recommendationId
    ) {
        return ResponseEntity.ok().body(recommendationService.findById(recommendationId));
    }

    @PostMapping
    public ResponseEntity<RecommendationDetailsResponse> addRecommendation(
            @Valid @RequestBody RecommendationCreateRequest req
    ) {
        return ResponseEntity.ok().body(recommendationService.addRecommendation(req));
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<RecommendationDetailsResponse> updateRecommendation(
            @PathVariable Long recommendationId,
            @Valid @RequestBody RecommendationUpdateRequest req
    ) {
        return ResponseEntity.ok().body(recommendationService.updateRecommendation(recommendationId, req));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(
            @PathVariable Long recommendationId
    ) {
        recommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.ok().build();
    }
}
