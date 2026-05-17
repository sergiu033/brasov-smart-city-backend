package com.smartcity.parking.controller;

import com.smartcity.parking.dto.request.ParkingPaymentRequest;
import com.smartcity.parking.dto.response.ParkingPaymentResponse;
import com.smartcity.parking.service.ParkingPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingPaymentControllerTest {

    @Mock
    private ParkingPaymentService parkingPaymentService;

    @InjectMocks
    private ParkingPaymentController parkingPaymentController;

    @Test
    void createPayment_delegatesToService() {
        ParkingPaymentRequest request = ParkingPaymentRequest.builder()
                .vehicleId(1L)
                .zoneCode("Z1")
                .durationHours(2)
                .build();
        var auth = UsernamePasswordAuthenticationToken.authenticated("ion@example.com", null, List.of());
        ParkingPaymentResponse response = ParkingPaymentResponse.builder()
                .id(1L)
                .totalPrice(new BigDecimal("10"))
                .build();

        when(parkingPaymentService.processPayment(request, "ion@example.com")).thenReturn(response);

        ResponseEntity<ParkingPaymentResponse> result = parkingPaymentController.createPayment(request, auth);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getMyPayments_delegatesToService() {
        var auth = UsernamePasswordAuthenticationToken.authenticated("ion@example.com", null, List.of());
        when(parkingPaymentService.getUserPayments("ion@example.com")).thenReturn(List.of());

        assertThat(parkingPaymentController.getMyPayments(auth).getBody()).isEmpty();
    }
}
