package com.smartcity.parking.repository;

import com.smartcity.parking.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {

    @Query("""
        SELECT p
        FROM ParkingZone p
        WHERE p.zoneCode = :zoneCode
    """)
    Optional<ParkingZone> findByZoneCode(@Param("zoneCode") String zoneCode);

    boolean existsByZoneCode(String zoneCode);
}
