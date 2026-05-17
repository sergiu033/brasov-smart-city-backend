package com.smartcity.recommendation.service;

import com.smartcity.exception.RecommendationCategoryNotFoundException;
import com.smartcity.exception.RecommendationNotFoundException;
import com.smartcity.recommendation.dto.request.RecommendationCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.entity.Recommendation;
import com.smartcity.recommendation.entity.RecommendationCategory;
import com.smartcity.recommendation.mapper.RecommendationMapper;
import com.smartcity.recommendation.repository.RecommendationCategoryRepository;
import com.smartcity.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationCategoryRepository recommendationCategoryRepository;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void findByCategory_generalReturnsAll() {
        Recommendation recommendation = Recommendation.builder()
                .id(1L)
                .title("Muzeu")
                .location("Centru")
                .category(RecommendationCategory.builder().code("culture").build())
                .build();
        when(recommendationRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(recommendation)));

        Page<com.smartcity.recommendation.dto.response.RecommendationResponse> page =
                recommendationService.findByCategory("general", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().category()).isEqualTo("culture");
    }

    @Test
    void findById_throwsWhenMissing() {
        when(recommendationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationService.findById(5L))
                .isInstanceOf(RecommendationNotFoundException.class);
    }

    @Test
    void findByCategory_throwsWhenCategoryMissing() {
        when(recommendationCategoryRepository.findByCode("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationService.findByCategory("unknown", PageRequest.of(0, 10)))
                .isInstanceOf(RecommendationCategoryNotFoundException.class);
    }

    @Test
    void addRecommendation_savesRecommendation() {
        RecommendationCategory category = RecommendationCategory.builder().id(1L).code("food").build();
        RecommendationCreateRequest request =
                new RecommendationCreateRequest("Restaurant", "Centru", "Bun", 1L);
        Recommendation saved = Recommendation.builder().id(2L).title("Restaurant").category(category).build();
        RecommendationDetailsResponse details =
                RecommendationDetailsResponse.builder().id(2L).title("Restaurant").build();

        when(recommendationCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(saved);
        when(recommendationMapper.recommendationToRecommendationDetailsResponse(saved)).thenReturn(details);

        assertThat(recommendationService.addRecommendation(request)).isEqualTo(details);
    }

    @Test
    void deleteRecommendation_deletesById() {
        recommendationService.deleteRecommendation(3L);
        verify(recommendationRepository).deleteById(3L);
    }
}
