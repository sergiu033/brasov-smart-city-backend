package com.smartcity.recommendation.repository;

import com.smartcity.recommendation.entity.Recommendation;
import com.smartcity.recommendation.entity.RecommendationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("""
        SELECT r
        FROM Recommendation r
        JOIN RecommendationCategory rc ON r.category.id = rc.id
    """)
    Page<Recommendation> findAll(Pageable pageable);

    Page<Recommendation> findByCategory(RecommendationCategory category, Pageable pageable);

}
