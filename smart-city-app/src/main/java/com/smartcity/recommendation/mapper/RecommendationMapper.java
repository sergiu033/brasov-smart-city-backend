package com.smartcity.recommendation.mapper;

import com.smartcity.recommendation.dto.request.RecommendationCreateRequest;
import com.smartcity.recommendation.dto.request.RecommendationUpdateRequest;
import com.smartcity.recommendation.dto.response.RecommendationDetailsResponse;
import com.smartcity.recommendation.dto.response.RecommendationResponse;
import com.smartcity.recommendation.entity.Recommendation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = RecommendationCategoryMapper.class)
public interface RecommendationMapper {

    Recommendation recommendationCreateRequestToRecommendation(RecommendationCreateRequest recommendationCreateRequest);
    Recommendation recommendationUpdateRequestToRecommendation(RecommendationUpdateRequest recommendationUpdateRequest);
    RecommendationDetailsResponse recommendationToRecommendationDetailsResponse(Recommendation recommendation);

}
