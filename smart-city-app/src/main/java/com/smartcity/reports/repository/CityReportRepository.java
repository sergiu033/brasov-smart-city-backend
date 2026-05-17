package com.smartcity.reports.repository;

import com.smartcity.reports.entity.CityReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityReportRepository extends JpaRepository<CityReport, Long> {

    Page<CityReport> findByUserId(Long userId, Pageable pageable);

    Page<CityReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<CityReport> findByCategoryIdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);
}
