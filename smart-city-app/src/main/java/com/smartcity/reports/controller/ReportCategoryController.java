package com.smartcity.reports.controller;

import com.smartcity.reports.dto.ReportCategoryRequest;
import com.smartcity.reports.dto.ReportCategoryResponse;
import com.smartcity.reports.service.ReportCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-categories")
@RequiredArgsConstructor
public class ReportCategoryController {

    private final ReportCategoryService reportCategoryService;

    @PostMapping
    public ResponseEntity<ReportCategoryResponse> createCategory(@RequestBody ReportCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportCategoryService.createCategory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportCategoryResponse> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(reportCategoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReportCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(reportCategoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportCategoryResponse> updateCategory(
            @PathVariable Long id, 
            @RequestBody ReportCategoryRequest request) {
        return ResponseEntity.ok(reportCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        reportCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
