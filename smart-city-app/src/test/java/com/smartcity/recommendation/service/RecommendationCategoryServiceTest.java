package com.smartcity.recommendation.service;

import com.smartcity.exception.RecommendationCategoryCodeAlreadyTaken;
import com.smartcity.exception.RecommendationCategoryNotFoundException;
import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.entity.RecommendationCategory;
import com.smartcity.recommendation.mapper.RecommendationCategoryMapper;
import com.smartcity.recommendation.repository.RecommendationCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationCategoryServiceTest {

    @Mock
    private RecommendationCategoryRepository recommendationCategoryRepository;
    @Mock
    private RecommendationCategoryMapper recommendationCategoryMapper;

    @InjectMocks
    private RecommendationCategoryService recommendationCategoryService;

    @Test
    void addRecommendationCategory_throwsWhenCodeTaken() {
        RecommendationCategoryCreateRequest request =
                new RecommendationCategoryCreateRequest("food", "Mancare");
        when(recommendationCategoryRepository.existsByCode("food")).thenReturn(true);

        assertThatThrownBy(() -> recommendationCategoryService.addRecommendationCategory(request))
                .isInstanceOf(RecommendationCategoryCodeAlreadyTaken.class);
    }

    @Test
    void findById_throwsWhenMissing() {
        when(recommendationCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationCategoryService.findById(1L))
                .isInstanceOf(RecommendationCategoryNotFoundException.class);
    }

    @Test
    void addRecommendationCategory_savesCategory() {
        RecommendationCategoryCreateRequest request =
                new RecommendationCategoryCreateRequest("food", "Mancare");
        RecommendationCategory mapped = RecommendationCategory.builder().code("food").build();
        RecommendationCategory saved = RecommendationCategory.builder().id(1L).code("food").build();
        RecommendationCategoryResponse response =
                RecommendationCategoryResponse.builder().id(1L).code("food").build();

        when(recommendationCategoryRepository.existsByCode("food")).thenReturn(false);
        when(recommendationCategoryMapper.recommendationCategoryCreateRequestToRecommendationCategory(request))
                .thenReturn(mapped);
        when(recommendationCategoryRepository.save(mapped)).thenReturn(saved);
        when(recommendationCategoryMapper.recommendationCategoryToRecommendationCategoryResponse(saved))
                .thenReturn(response);

        recommendationCategoryService.addRecommendationCategory(request);

        verify(recommendationCategoryRepository).save(mapped);
    }
}
