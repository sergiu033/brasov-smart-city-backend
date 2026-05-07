package com.smartcity.recommendation.mapper;

import com.smartcity.recommendation.dto.request.RecommendationCategoryCreateRequest;
import com.smartcity.recommendation.dto.response.RecommendationCategoryResponse;
import com.smartcity.recommendation.entity.RecommendationCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecommendationCategoryMapper {

    RecommendationCategoryResponse recommendationCategoryToRecommendationCategoryResponse(RecommendationCategory recommendationCategory);
    RecommendationCategory recommendationCategoryCreateRequestToRecommendationCategory(RecommendationCategoryCreateRequest recommendationCategoryCreateRequest);
}
