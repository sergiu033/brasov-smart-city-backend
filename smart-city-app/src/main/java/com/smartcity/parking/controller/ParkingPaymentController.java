package com.smartcity.parking.controller;

import com.smartcity.parking.dto.request.ParkingPaymentRequest;
import com.smartcity.parking.dto.response.ParkingPaymentResponse;
import com.smartcity.parking.service.ParkingPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/payments")
@RequiredArgsConstructor
public class ParkingPaymentController {

    private final ParkingPaymentService parkingPaymentService;

    @PostMapping
    public ResponseEntity<ParkingPaymentResponse> createPayment(
            @Valid @RequestBody ParkingPaymentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(parkingPaymentService.processPayment(request, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ParkingPaymentResponse>> getMyPayments(Authentication authentication) {
        return ResponseEntity.ok(parkingPaymentService.getUserPayments(authentication.getName()));
    }
}
