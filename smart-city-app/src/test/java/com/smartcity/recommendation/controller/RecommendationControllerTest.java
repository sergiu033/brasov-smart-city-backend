package com.smartcity.recommendation.controller;

import com.smartcity.recommendation.dto.request.RecommendationCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.dto.response.RecommendationResponse;
import com.smartcity.recommendation.service.RecommendationService;
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
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void getRecommendationsByCategoryCode_usesDefaultGeneral() {
        Page<RecommendationResponse> page = new PageImpl<>(List.of());
        PageRequest pageable = PageRequest.of(0, 10);
        when(recommendationService.findByCategory("general", pageable)).thenReturn(page);

        assertThat(recommendationController.getRecommendationsByCategoryCode("general", pageable).getBody())
                .isEqualTo(page);
    }

    @Test
    void getRecommendationDetails_delegatesToService() {
        RecommendationDetailsResponse details =
                RecommendationDetailsResponse.builder().id(1L).title("Muzeu").build();
        when(recommendationService.findById(1L)).thenReturn(details);

        assertThat(recommendationController.getRecommendationDetails(1L).getBody()).isEqualTo(details);
    }

    @Test
    void deleteRecommendation_delegatesToService() {
        recommendationController.deleteRecommendation(4L);

        verify(recommendationService).deleteRecommendation(4L);
    }
}
