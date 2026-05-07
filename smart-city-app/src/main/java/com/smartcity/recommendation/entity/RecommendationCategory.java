package com.smartcity.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="recommendation_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;
}
