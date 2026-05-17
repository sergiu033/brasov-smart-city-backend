package com.smartcity.parking.service;

import com.smartcity.exception.ParkingZoneNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.exception.VehicleNotFoundException;
import com.smartcity.notification.enums.NotificationType;
import com.smartcity.notification.service.NotificationService;
import com.smartcity.parking.dto.request.ParkingPaymentRequest;
import com.smartcity.parking.dto.response.ParkingPaymentResponse;
import com.smartcity.parking.entity.ParkingPayment;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.repository.ParkingPaymentRepository;
import com.smartcity.parking.repository.ParkingZoneRepository;
import com.smartcity.user.entity.User;
import com.smartcity.user_vehicles.entity.Vehicle;
import com.smartcity.user.repository.UserRepository;
import com.smartcity.user_vehicles.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingPaymentService {

    private final ParkingPaymentRepository parkingPaymentRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final VehicleRepository vehicleRepository;

    private Vehicle getVehicleByIdOrThrow(Long id) {
        return vehicleRepository.findById(id).orElseThrow(
                () -> new VehicleNotFoundException("Vehicle not found for id: " + id)
        );
    }

    @Transactional
    public ParkingPaymentResponse processPayment(ParkingPaymentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ParkingZone zone = parkingZoneRepository.findByZoneCode(request.zoneCode())
                .orElseThrow(() -> new ParkingZoneNotFoundException("Parking zone not found"));

        BigDecimal totalPrice = zone.getTariffPerHour().multiply(new BigDecimal(request.durationHours()));

        Vehicle vehicle = getVehicleByIdOrThrow(request.vehicleId());

        ParkingPayment payment = ParkingPayment.builder()
                .user(user)
                .zone(zone)
                .vehicle(vehicle)
                .durationHours(request.durationHours())
                .totalPrice(totalPrice)
                .isPaid(true)
                .latitude(request.latitude())
                .longitude(request.longitude())
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

    @Transactional(readOnly = true)
    public List<ParkingPaymentResponse> getUserPayments(String userEmail) {
        return parkingPaymentRepository.findByUserEmail(userEmail).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ParkingPaymentResponse mapToResponse(ParkingPayment payment) {
        return ParkingPaymentResponse.builder()
                .id(payment.getId())
                .plateNumber(payment.getVehicle().getPlateNumber())
                .zoneCode(payment.getZone().getZoneCode())
                .durationHours(payment.getDurationHours())
                .totalPrice(payment.getTotalPrice())
                .isPaid(payment.getIsPaid())
                .paymentDate(payment.getPaymentDate())
                .status("CONFIRMED")
                .build();
    }
}
