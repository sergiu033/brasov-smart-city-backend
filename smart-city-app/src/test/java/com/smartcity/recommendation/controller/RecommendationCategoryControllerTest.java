package com.smartcity.recommendation.controller;

import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.service.RecommendationCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationCategoryControllerTest {

    @Mock
    private RecommendationCategoryService recommendationCategoryService;

    @InjectMocks
    private RecommendationCategoryController recommendationCategoryController;

    @Test
    void findById_delegatesToService() {
        RecommendationCategoryResponse response =
                RecommendationCategoryResponse.builder().id(1L).code("food").build();
        when(recommendationCategoryService.findById(1L)).thenReturn(response);

        assertThat(recommendationCategoryController.findById(1L).getBody()).isEqualTo(response);
    }

    @Test
    void addRecommendationCategory_delegatesToService() {
        RecommendationCategoryCreateRequest request =
                new RecommendationCategoryCreateRequest("food", "Mancare");
        RecommendationCategoryResponse response =
                RecommendationCategoryResponse.builder().id(1L).code("food").build();
        when(recommendationCategoryService.addRecommendationCategory(request)).thenReturn(response);

        ResponseEntity<RecommendationCategoryResponse> result =
                recommendationCategoryController.addRecommendationCategory(request);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteRecommendationCategory_delegatesToService() {
        recommendationCategoryController.deleteRecommendationCategory(2L);

        verify(recommendationCategoryService).deleteRecommendationCategory(2L);
    }
}
