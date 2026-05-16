package com.smartcity.parking.service;

import com.smartcity.exception.ParkingZoneNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.notification.enums.NotificationType;
import com.smartcity.notification.service.NotificationService;
import com.smartcity.parking.dto.request.ParkingPaymentRequest;
import com.smartcity.parking.dto.response.ParkingPaymentResponse;
import com.smartcity.parking.entity.ParkingPayment;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.repository.ParkingPaymentRepository;
import com.smartcity.parking.repository.ParkingZoneRepository;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingPaymentService {

    private final ParkingPaymentRepository parkingPaymentRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public ParkingPaymentResponse processPayment(ParkingPaymentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ParkingZone zone = parkingZoneRepository.findByZoneCode(request.getZoneCode())
                .orElseThrow(() -> new ParkingZoneNotFoundException("Parking zone not found"));

        BigDecimal totalPrice = zone.getTariffPerHour().multiply(new BigDecimal(request.getDurationHours()));

        ParkingPayment payment = ParkingPayment.builder()
                .user(user)
                .zone(zone)
                .plateNumber(request.getPlateNumber())
                .durationHours(request.getDurationHours())
                .totalPrice(totalPrice)
                .isPaid(true) // Simulating successful payment
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        ParkingPayment savedPayment = parkingPaymentRepository.save(payment);

        notificationService.sendNotification(
                savedPayment.getUser().getEmail(),
                "Plata înregistrată",
                "Plata parcării în zona " + savedPayment.getZone().getZoneCode() + " în data de " + Instant.now(),
                NotificationType.REPORT_STATUS_CHANGE
        );

        return mapToResponse(savedPayment);
    }

    public List<ParkingPaymentResponse> getUserPayments(String userEmail) {
        return parkingPaymentRepository.findByUserEmail(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ParkingPaymentResponse mapToResponse(ParkingPayment payment) {
        return ParkingPaymentResponse.builder()
                .id(payment.getId())
                .plateNumber(payment.getPlateNumber())
                .zoneCode(payment.getZone().getZoneCode())
                .durationHours(payment.getDurationHours())
                .totalPrice(payment.getTotalPrice())
                .isPaid(payment.getIsPaid())
                .paymentDate(payment.getPaymentDate())
                .status("CONFIRMED")
                .build();
    }
}
