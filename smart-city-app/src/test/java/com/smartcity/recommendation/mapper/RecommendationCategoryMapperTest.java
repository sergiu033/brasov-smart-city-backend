package com.smartcity.recommendation.mapper;

import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.entity.RecommendationCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RecommendationCategoryMapperImpl.class)
class RecommendationCategoryMapperTest {

    @Autowired
    private RecommendationCategoryMapper mapper;

    @Test
    void mapsCategoryFields() {
        RecommendationCategoryCreateRequest request =
                new RecommendationCategoryCreateRequest("food", "Restaurante");

        RecommendationCategory entity = mapper.recommendationCategoryCreateRequestToRecommendationCategory(request);
        entity.setId(1L);

        RecommendationCategoryResponse response =
                mapper.recommendationCategoryToRecommendationCategoryResponse(entity);

        assertThat(entity.getCode()).isEqualTo("food");
        assertThat(entity.getDisplayName()).isEqualTo("Restaurante");
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("food");
    }
}
