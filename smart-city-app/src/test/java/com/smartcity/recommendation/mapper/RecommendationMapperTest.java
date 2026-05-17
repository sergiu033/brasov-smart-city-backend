package com.smartcity.recommendation.mapper;

import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.entity.Recommendation;
import com.smartcity.recommendation.entity.RecommendationCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RecommendationMapperImpl.class, RecommendationCategoryMapperImpl.class})
class RecommendationMapperTest {

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Test
    void recommendationToRecommendationDetailsResponse_mapsFields() {
        RecommendationCategory category = RecommendationCategory.builder()
                .id(1L)
                .code("culture")
                .displayName("Cultura")
                .build();
        Recommendation recommendation = Recommendation.builder()
                .id(5L)
                .category(category)
                .title("Muzeu")
                .location("Centru")
                .description("Vizita ghidata")
                .build();

        RecommendationDetailsResponse response =
                recommendationMapper.recommendationToRecommendationDetailsResponse(recommendation);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.title()).isEqualTo("Muzeu");
        assertThat(response.location()).isEqualTo("Centru");
    }
}
