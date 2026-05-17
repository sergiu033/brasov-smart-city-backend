package com.smartcity.user_vehicles.mapper;

import com.smartcity.user_vehicles.dto.VehicleResponse;
import com.smartcity.user_vehicles.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {

    VehicleResponse vehicleToVehicleResponse(Vehicle vehicle);

}
