package com.smartcity.parking.mapper;


import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.request.ParkingZoneUpdateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.dto.response.ParkingZoneResponse;
import com.smartcity.parking.entity.ParkingZone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParkingZoneMapper {

    ParkingZone parkingZoneCreateRequestToParkingZone(ParkingZoneCreateRequest parkingZoneCreateRequest);
    ParkingZoneResponse parkingZoneToParkingZoneResponse(ParkingZone parkingZone);
    ParkingZoneDetailsResponse parkingZoneToParkingZoneDetailsResponse(ParkingZone parkingZone);
    ParkingZone parkingZoneUpdateRequestToParkingZone(ParkingZoneUpdateRequest parkingZoneUpdateRequest);
}
