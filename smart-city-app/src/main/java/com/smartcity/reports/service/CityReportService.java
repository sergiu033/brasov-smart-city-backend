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
import com.smartcity.reports.enums.ReportStatus;
import com.smartcity.reports.mapper.CityReportMapper;
import com.smartcity.reports.repository.CityReportRepository;
import com.smartcity.reports.repository.ReportCategoryRepository;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.smartcity.imageservice.ImageStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class CityReportService {

    private final CityReportMapper cityReportMapper;
    private final CityReportRepository cityReportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ImageStorageService imageStorageService;

    @Transactional
    public CityReportResponse createReport(CityReportRequest cityReportRequest, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userEmail));

        ReportCategory reportCategory = reportCategoryRepository.findById(cityReportRequest.categoryId())
                .orElseThrow(() -> new ReportCategoryNotFoundException("Report category with id: " + cityReportRequest.categoryId() + " not found"));

        String filePath = null;
        if (cityReportRequest.image() != null && !cityReportRequest.image().isEmpty()) {
            try {
                InputStream image = cityReportRequest.image().getInputStream();
                filePath = imageStorageService.saveImage(image, cityReportRequest.image().getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CityReport newReport = cityReportMapper.toEntity(cityReportRequest);
        if (filePath != null) {
            newReport.setPhotoUrl(filePath);
        }

        newReport.setUser(user);
        newReport.setCategory(reportCategory);

        return cityReportMapper.toResponse(
                cityReportRepository.save(newReport)
        );
    }

    @Transactional(readOnly = true)
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

        ReportStatus oldStatus = cityReport.getStatus();
        ReportStatus newStatus = request.status();

        cityReport.setCategory(reportCategory);
        cityReport.setDescription(request.description());
        cityReport.setLatitude(request.latitude());
        cityReport.setLongitude(request.longitude());
        cityReport.setStatus(request.status());

        if (request.image() != null && !request.image().isEmpty()) {
            try {
                InputStream image = request.image().getInputStream();
                String filePath = imageStorageService.saveImage(image, request.image().getOriginalFilename());
                cityReport.setPhotoUrl(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CityReport updatedReport = cityReportRepository.save(cityReport);

        if (oldStatus != newStatus) {
            notificationService.sendNotification(
                    cityReport.getUser().getEmail(),
                    "Actualizare Status Sesizare",
                    String.format("Statusul sesizării tale '%s' a fost actualizat din %s în %s.", 
                            reportCategory.getName(), oldStatus, newStatus),
                    NotificationType.REPORT_STATUS_CHANGE
            );
        }

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
    public Page<CityReportResponse> getAllReports(Pageable pageable) {
        return cityReportRepository.findAll(pageable).map(cityReportMapper::toResponse);
    }
}
