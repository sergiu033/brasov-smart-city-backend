package com.smartcity.reports.service;

import com.smartcity.exception.CityReportNotFoundException;
import com.smartcity.exception.ReportCategoryNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.reports.dto.CityReportRequest;
import com.smartcity.reports.dto.CityReportResponse;
import com.smartcity.reports.entity.CityReport;
import com.smartcity.reports.entity.ReportCategory;
import com.smartcity.reports.mapper.CityReportMapper;
import com.smartcity.reports.repository.CityReportRepository;
import com.smartcity.reports.repository.ReportCategoryRepository;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityReportService {

    private final CityReportMapper cityReportMapper;
    private final CityReportRepository cityReportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CityReportResponse createReport(CityReportRequest cityReportRequest, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        CityReport newReport = cityReportMapper.toEntity(cityReportRequest);

        newReport.setUser(user);

        return cityReportMapper.toResponse(
                cityReportRepository.save(newReport)
        );
    }

    public CityReportResponse getReportById(Long reportId) {

        CityReport cityReport = cityReportRepository.findById(reportId)
                .orElseThrow(() -> new CityReportNotFoundException("Report with id: " + reportId + " not found"));

        return cityReportMapper.toResponse(cityReport);
    }

    @Transactional
    public CityReportResponse updateReport(Long reportId, CityReportRequest request) {

        CityReport cityReport = cityReportRepository.findById(reportId)
                .orElseThrow(() -> new CityReportNotFoundException("Report with id: " + reportId + " not found"));

        ReportCategory reportCategory = reportCategoryRepository.findById(request.categoryId())
                        .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + request.categoryId() + " not found"));

        cityReport.setCategory(reportCategory);
        cityReport.setDescription(request.description());
        cityReport.setLatitude(request.latitude());
        cityReport.setLongitude(request.longitude());
        cityReport.setStatus(request.status());
        cityReport.setPhotoUrl(request.photoUrl());

        return cityReportMapper.toResponse(cityReportRepository.save(cityReport));
    }

    public void deleteReport(Long reportId) {
        cityReportRepository.deleteById(reportId);
    }

    public Page<CityReportResponse> getCurrentUserReports(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        return cityReportRepository.findByUserId(user.getId(), pageable).map(cityReportMapper::toResponse);
    }

    public Page<CityReportResponse> getAllReports(Pageable pageable) {
        return cityReportRepository.findAll(pageable).map(cityReportMapper::toResponse);
    }
}
