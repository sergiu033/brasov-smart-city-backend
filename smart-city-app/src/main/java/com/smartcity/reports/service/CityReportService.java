package com.smartcity.reports.service;

import com.smartcity.exception.CityReportNotFoundException;
import com.smartcity.exception.ReportCategoryNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.notification.enums.NotificationType;
import com.smartcity.notification.service.NotificationService;
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
import com.smartcity.imagestorage.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityReportService {

    private final CityReportMapper cityReportMapper;
    private final CityReportRepository cityReportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ImageService imageService;

    private static final String NOT_FOUND = " not found";

    @Transactional
    public CityReportResponse createReport(CityReportRequest cityReportRequest, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        ReportCategory reportCategory = reportCategoryRepository.findById(cityReportRequest.categoryId())
                .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + cityReportRequest.categoryId() + NOT_FOUND));

        String filePath = null;
        if (cityReportRequest.image() != null && !cityReportRequest.image().isEmpty()) {
            try {
                InputStream image = cityReportRequest.image().getInputStream();
                filePath = imageService.saveImage(image, cityReportRequest.image().getOriginalFilename());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        CityReport newReport = cityReportMapper.toEntity(cityReportRequest);
        if (filePath != null) {
            newReport.setPhotoUrl(filePath);
        }

        newReport.setUser(user);
        newReport.setCategory(reportCategory);

        notificationService.sendNotification(
                newReport.getUser().getEmail(),
                "Sesizare creată",
                "Sesizarea a fost creată în data de " + Instant.now(),
                NotificationType.REPORT_STATUS_CHANGE
        );

        return cityReportMapper.toResponse(
                cityReportRepository.save(newReport)
        );
    }

    @Transactional(readOnly = true)
    public CityReportResponse getReportById(Long reportId) {

        CityReport cityReport = cityReportRepository.findById(reportId)
                .orElseThrow(() -> new CityReportNotFoundException("Report with id: " + reportId + NOT_FOUND));

        return cityReportMapper.toResponse(cityReport);
    }

    @Transactional
    public CityReportResponse updateReport(Long reportId, CityReportRequest request) {

        CityReport cityReport = cityReportRepository.findById(reportId)
                .orElseThrow(() -> new CityReportNotFoundException("Report with id: " + reportId + NOT_FOUND));

        ReportCategory reportCategory = reportCategoryRepository.findById(request.categoryId())
                        .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + request.categoryId() + NOT_FOUND));

        cityReport.setCategory(reportCategory);
        cityReport.setDescription(request.description());
        cityReport.setLatitude(request.latitude());
        cityReport.setLongitude(request.longitude());
        cityReport.setStatus(request.status());

        if (request.image() != null && !request.image().isEmpty()) {
            try {
                InputStream image = request.image().getInputStream();
                String filePath = imageService.saveImage(image, request.image().getOriginalFilename());
                cityReport.setPhotoUrl(filePath);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        CityReport updatedReport = cityReportRepository.save(cityReport);

        notificationService.sendNotification(
                cityReport.getUser().getEmail(),
                "Sesizare actualizată",
                "Sesizarea a fost actualizată in data de " + Instant.now(),
                NotificationType.REPORT_STATUS_CHANGE
        );

        return cityReportMapper.toResponse(updatedReport);
    }

    public void deleteReport(Long reportId) {
        cityReportRepository.deleteById(reportId);
    }

    @Transactional(readOnly = true)
    public Page<CityReportResponse> getCurrentUserReports(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        return cityReportRepository.findByUserId(user.getId(), pageable).map(cityReportMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CityReportResponse> getAllReports(Long categoryId, Pageable pageable) {
        Page<CityReport> reports = categoryId == null
                ? cityReportRepository.findAllByOrderByCreatedAtDesc(pageable)
                : cityReportRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
        return reports.map(cityReportMapper::toResponse);
    }
}
