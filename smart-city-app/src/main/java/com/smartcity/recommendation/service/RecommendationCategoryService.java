package com.smartcity.recommendation.service;

import com.smartcity.exception.RecommendationCategoryCodeAlreadyTaken;
import com.smartcity.exception.RecommendationCategoryNotFoundException;
import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.request.RecommendationCategoryUpdateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.entity.RecommendationCategory;
import com.smartcity.recommendation.mapper.RecommendationCategoryMapper;
import com.smartcity.recommendation.repository.RecommendationCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationCategoryService {

    private final RecommendationCategoryRepository recommendationCategoryRepository;
    private final RecommendationCategoryMapper recommendationCategoryMapper;

    private RecommendationCategory getByCodeOrByIdOrThrow(String code, Long id, boolean byId) {
        return byId
                ? recommendationCategoryRepository.findById(id).orElseThrow(
                        () -> new RecommendationCategoryNotFoundException("Categoria de recomandari nu a fost gasita pentru id-ul: " + id)
                )
                : recommendationCategoryRepository.findByCode(code).orElseThrow(
                        () -> new RecommendationCategoryNotFoundException("Categoria de recomandari nu a fost gasita pentru codul: " + code)
                );
    }

    public Page<RecommendationCategoryResponse> findAll(Pageable pageable) {
        return recommendationCategoryRepository
                .findAll(pageable)
                .map(recommendationCategoryMapper::recommendationCategoryToRecommendationCategoryResponse);
    }

    public RecommendationCategoryResponse findById(Long id) {
        RecommendationCategory recommendationCategory = getByCodeOrByIdOrThrow(null, id, true);
        return recommendationCategoryMapper.recommendationCategoryToRecommendationCategoryResponse(recommendationCategory);
    }

    @Transactional
    public RecommendationCategoryResponse addRecommendationCategory(RecommendationCategoryCreateRequest req) {

        if (recommendationCategoryRepository.existsByCode(req.code())) {
            throw new RecommendationCategoryCodeAlreadyTaken("Codul '"+ req.code() +"' este deja utilizat.");
        }

        RecommendationCategory recommendationCategory = recommendationCategoryMapper.recommendationCategoryCreateRequestToRecommendationCategory(req);
        RecommendationCategory savedRecommendationCategory = recommendationCategoryRepository.save(recommendationCategory);

        return recommendationCategoryMapper.recommendationCategoryToRecommendationCategoryResponse(savedRecommendationCategory);
    }

    @Transactional
    public RecommendationCategoryResponse updateRecommendationCategory(Long id, RecommendationCategoryUpdateRequest req) {
        RecommendationCategory recommendationCategory = getByCodeOrByIdOrThrow(null, id, true);

        recommendationCategory.setCode(req.code());
        recommendationCategory.setDisplayName(req.displayName());

        return recommendationCategoryMapper.recommendationCategoryToRecommendationCategoryResponse(recommendationCategoryRepository.save(recommendationCategory));
    }

    @Transactional
    public void deleteRecommendationCategory(Long id) {
        recommendationCategoryRepository.deleteById(id);
    }

}
