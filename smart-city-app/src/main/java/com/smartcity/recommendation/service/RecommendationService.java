package com.smartcity.recommendation.service;

import com.smartcity.exception.RecommendationCategoryNotFoundException;
import com.smartcity.exception.RecommendationNotFoundException;
import com.smartcity.recommendation.dto.request.RecommendationCreateRequest;
import com.smartcity.recommendation.dto.request.RecommendationUpdateRequest;
import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.dto.response.RecommendationResponse;
import com.smartcity.recommendation.entity.Recommendation;
import com.smartcity.recommendation.entity.RecommendationCategory;
import com.smartcity.recommendation.mapper.RecommendationMapper;
import com.smartcity.recommendation.repository.RecommendationCategoryRepository;
import com.smartcity.recommendation.repository.RecommendationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationCategoryRepository recommendationCategoryRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    private Recommendation getByIdOrThrow(Long id) {
        return recommendationRepository
                .findById(id).orElseThrow(
                        () -> new RecommendationNotFoundException("Nu a fost gasita recomandarea pentru id-ul: " + id)
                );
    }

    private RecommendationCategory getCategoryByCodeOrIdOrThrow(String code, Long id, boolean byId) {
        return byId
                ? recommendationCategoryRepository
                .findById(id).orElseThrow(
                        () -> new RecommendationCategoryNotFoundException(
                        "Categoria de recomandari nu a fost gasita pentru id-ul: " + id)
                )
                : recommendationCategoryRepository
                .findByCode(code).orElseThrow(
                        () -> new RecommendationCategoryNotFoundException(
                                "Categoria de recomandari nu a fost gasita pentru codul: " + code)
                );
    }

    public Page<RecommendationResponse> findByCategory(String categoryCode, Pageable pageable) {
        if (categoryCode != null && categoryCode.equalsIgnoreCase("general")) {
            return recommendationRepository
                    .findAll(pageable)
                    .map(r -> RecommendationResponse.builder()
                            .id(r.getId())
                            .category(r.getCategory().getCode())
                            .title(r.getTitle())
                            .location(r.getLocation())
                            .build());
        }
        RecommendationCategory category = getCategoryByCodeOrIdOrThrow(categoryCode, null, false);
        return recommendationRepository
                .findByCategory(category, pageable)
                .map(r -> RecommendationResponse.builder()
                        .id(r.getId())
                        .category(r.getCategory().getCode())
                        .title(r.getTitle())
                        .location(r.getLocation())
                        .build());
    }

    public RecommendationDetailsResponse findById(Long recommendationId) {
        Recommendation recommendation = getByIdOrThrow(recommendationId);

        return recommendationMapper.recommendationToRecommendationDetailsResponse(recommendation);
    }

    @Transactional
    public RecommendationDetailsResponse addRecommendation(RecommendationCreateRequest req) {
        RecommendationCategory category = getCategoryByCodeOrIdOrThrow(null, req.recommendationCategoryId(), true);
        Recommendation recommendation = Recommendation.builder()
                .title(req.title())
                .description(req.description())
                .location(req.location())
                .category(category)
                .build();

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        return recommendationMapper.recommendationToRecommendationDetailsResponse(savedRecommendation);
    }

    @Transactional
    public RecommendationDetailsResponse updateRecommendation(Long id, RecommendationUpdateRequest req) {
        RecommendationCategory category = getCategoryByCodeOrIdOrThrow(null, req.recommendationCategoryId(), true);
        Recommendation recommendation = getByIdOrThrow(id);

        recommendation.setTitle(req.title());
        recommendation.setDescription(req.description());
        recommendation.setLocation(req.location());
        recommendation.setCategory(category);

        return recommendationMapper.recommendationToRecommendationDetailsResponse(recommendation);
    }

    @Transactional
    public void deleteRecommendation(Long id) {
        recommendationRepository.deleteById(id);
    }

}
