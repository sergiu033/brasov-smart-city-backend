package com.smartcity.parking.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;

import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.request.ParkingZoneUpdateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.dto.response.ParkingZoneResponse;
import com.smartcity.parking.repository.ParkingZoneRepository;
import com.smartcity.parking.service.ParkingZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingZoneController {

    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingZoneService parkingZoneService;

    @GetMapping("/zones")
    public ApiResponse<Page<ParkingZoneResponse>> zones(Pageable pageable) {
        return ApiResponse.success(
                parkingZoneService.findAll(pageable),
                "Zone de parcare."
        );
    }

    @GetMapping("/zones/{zoneCode}")
    public ApiResponse<ParkingZoneDetailsResponse> zoneDetails(@PathVariable String zoneCode) {
        return ApiResponse.success(
                parkingZoneService.findByZoneCode(zoneCode),
                "Detalii zona de parcare."
        );
    }

    @PostMapping("/zones")
    public ApiResponse<ParkingZoneDetailsResponse> addParkingZone(@Valid @RequestBody ParkingZoneCreateRequest req) {
        return ApiResponse.success(
                parkingZoneService.addParkingZone(req),
                "Zona de parcare a fost adaugata cu succes."
        );
    }

    @PutMapping("/zones/{zoneId}")
    public ApiResponse<ParkingZoneDetailsResponse> updateParkingZone(
            @PathVariable Long zoneId,
            @Valid @RequestBody ParkingZoneUpdateRequest req) {
            return ApiResponse.success(
                    parkingZoneService.updateParkingZone(zoneId, req),
                    "Zona de parcare a fost actualizata cu succes."
            );
    }

    @DeleteMapping("zones/{zoneId}")
    public ApiResponse<Void> deleteParkingZone(@PathVariable Long zoneId) {
        parkingZoneService.deleteParkingZone(zoneId);
        return ApiResponse.successMessage(
                "Zona de parcare a fost stearsa cu succes."
        );
    }
}
