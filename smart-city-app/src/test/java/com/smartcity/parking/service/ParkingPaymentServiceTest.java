package com.smartcity.parking.service;

import com.smartcity.exception.ParkingZoneNotFoundException;
import com.smartcity.exception.UserNotFoundException;
import com.smartcity.notification.service.NotificationService;
import com.smartcity.parking.dto.request.ParkingPaymentRequest;
import com.smartcity.parking.dto.response.ParkingPaymentResponse;
import com.smartcity.parking.entity.ParkingPayment;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.repository.ParkingPaymentRepository;
import com.smartcity.parking.repository.ParkingZoneRepository;
import com.smartcity.user.entity.Role;
import com.smartcity.user.entity.User;
import com.smartcity.user.repository.UserRepository;
import com.smartcity.user_vehicles.entity.Vehicle;
import com.smartcity.user_vehicles.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingPaymentServiceTest {

    @Mock
    private ParkingPaymentRepository parkingPaymentRepository;
    @Mock
    private ParkingZoneRepository parkingZoneRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ParkingPaymentService parkingPaymentService;

    @Test
    void processPayment_throwsWhenUserMissing() {
        ParkingPaymentRequest request = ParkingPaymentRequest.builder()
                .vehicleId(1L)
                .zoneCode("Z1")
                .durationHours(2)
                .build();
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingPaymentService.processPayment(request, "missing@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void processPayment_throwsWhenZoneMissing() {
        User user = sampleUser();
        ParkingPaymentRequest request = ParkingPaymentRequest.builder()
                .vehicleId(1L)
                .zoneCode("Z9")
                .durationHours(2)
                .build();

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(parkingZoneRepository.findByZoneCode("Z9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingPaymentService.processPayment(request, "ion@example.com"))
                .isInstanceOf(ParkingZoneNotFoundException.class);
    }

    @Test
    void processPayment_calculatesPriceAndNotifies() {
        User user = sampleUser();
        ParkingZone zone = ParkingZone.builder()
                .zoneCode("Z1")
                .tariffPerHour(new BigDecimal("5.00"))
                .build();
        Vehicle vehicle = Vehicle.builder().id(3L).plateNumber("BV 01 ABC").user(user).build();
        ParkingPaymentRequest request = ParkingPaymentRequest.builder()
                .vehicleId(3L)
                .zoneCode("Z1")
                .durationHours(2)
                .build();
        ParkingPayment saved = ParkingPayment.builder()
                .id(1L)
                .user(user)
                .zone(zone)
                .vehicle(vehicle)
                .durationHours(2)
                .totalPrice(new BigDecimal("10.00"))
                .isPaid(true)
                .build();

        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(parkingZoneRepository.findByZoneCode("Z1")).thenReturn(Optional.of(zone));
        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        when(parkingPaymentRepository.save(any(ParkingPayment.class))).thenReturn(saved);

        ParkingPaymentResponse response = parkingPaymentService.processPayment(request, "ion@example.com");

        assertThat(response.totalPrice()).isEqualByComparingTo("10.00");
        assertThat(response.status()).isEqualTo("CONFIRMED");
        verify(notificationService).sendNotification(eq("ion@example.com"), any(), any(), any());
    }

    @Test
    void getUserPayments_returnsMappedList() {
        User user = sampleUser();
        ParkingZone zone = ParkingZone.builder().zoneCode("Z1").build();
        Vehicle vehicle = Vehicle.builder().plateNumber("BV 01 ABC").build();
        ParkingPayment payment = ParkingPayment.builder()
                .id(1L)
                .user(user)
                .zone(zone)
                .vehicle(vehicle)
                .durationHours(1)
                .totalPrice(new BigDecimal("5"))
                .isPaid(true)
                .build();

        when(parkingPaymentRepository.findByUserEmail("ion@example.com")).thenReturn(List.of(payment));

        assertThat(parkingPaymentService.getUserPayments("ion@example.com")).hasSize(1);
    }

    private static User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ion@example.com");
        user.setRole(Role.CITIZEN);
        return user;
    }
}
