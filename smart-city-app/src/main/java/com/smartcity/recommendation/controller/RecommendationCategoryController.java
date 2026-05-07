package com.smartcity.recommendation.controller;

import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.request.RecommendationCategoryUpdateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.service.RecommendationCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation/categories")
@RequiredArgsConstructor
public class RecommendationCategoryController {

    private final RecommendationCategoryService recommendationCategoryService;

    @GetMapping
    public ResponseEntity<Page<RecommendationCategoryResponse>> getAllRecommendationCategories(Pageable pageable) {
        return ResponseEntity.ok().body(recommendationCategoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(recommendationCategoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RecommendationCategoryResponse> addRecommendationCategory(@Valid @RequestBody RecommendationCategoryCreateRequest req) {
        return ResponseEntity.ok().body(recommendationCategoryService.addRecommendationCategory(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecommendationCategoryResponse> updateRecommendationCategory(
            @PathVariable Long id,
            @Valid @RequestBody RecommendationCategoryUpdateRequest req
    ) {
        return ResponseEntity.ok().body(recommendationCategoryService.updateRecommendationCategory(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendationCategory(@PathVariable Long id) {
        recommendationCategoryService.deleteRecommendationCategory(id);
        return ResponseEntity.ok().build();
    }

}
