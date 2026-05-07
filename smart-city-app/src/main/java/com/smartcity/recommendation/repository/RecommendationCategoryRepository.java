package com.smartcity.recommendation.repository;

import com.smartcity.recommendation.entity.RecommendationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationCategoryRepository extends JpaRepository<RecommendationCategory, Long> {

    Optional<RecommendationCategory> findByCode(String code);
    boolean existsByCode(String code);

}
