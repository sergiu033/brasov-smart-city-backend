package com.smartcity.user_vehicles.repository;

import com.smartcity.user_vehicles.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
