package com.smartcity.reports.repository;

import com.smartcity.reports.entity.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {
}
