package com.smartcity.parking.repository;

import com.smartcity.parking.entity.ParkingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingPaymentRepository extends JpaRepository<ParkingPayment, Long> {
    List<ParkingPayment> findByUserEmail(String email);
}
