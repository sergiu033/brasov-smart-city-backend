package com.smartcity.parking.controller;

import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.request.ParkingZoneUpdateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.dto.response.ParkingZoneResponse;
import com.smartcity.parking.service.ParkingZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingZoneController {

    private final ParkingZoneService parkingZoneService;

    @GetMapping("/zones")
    public ResponseEntity<Page<ParkingZoneResponse>> zones(Pageable pageable) {
        return ResponseEntity.ok(parkingZoneService.findAll(pageable));
    }

    @GetMapping("/zones/{zoneCode}")
    public ResponseEntity<ParkingZoneDetailsResponse> zoneDetails(@PathVariable String zoneCode) {
        return ResponseEntity.ok(parkingZoneService.findByZoneCode(zoneCode));
    }

    @PostMapping("/zones")
    public ResponseEntity<ParkingZoneDetailsResponse> addParkingZone(@Valid @RequestBody ParkingZoneCreateRequest req) {
        return ResponseEntity.ok(parkingZoneService.addParkingZone(req));
    }

    @PutMapping("/zones/{zoneId}")
    public ResponseEntity<ParkingZoneDetailsResponse> updateParkingZone(
            @PathVariable Long zoneId,
            @Valid @RequestBody ParkingZoneUpdateRequest req) {
        return ResponseEntity.ok(parkingZoneService.updateParkingZone(zoneId, req));
    }

    @DeleteMapping("zones/{zoneId}")
    public ResponseEntity<Void> deleteParkingZone(@PathVariable Long zoneId) {
        parkingZoneService.deleteParkingZone(zoneId);
        return ResponseEntity.noContent().build();
    }
}
