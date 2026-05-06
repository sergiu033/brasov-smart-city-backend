package com.smartcity.parking.service;

import com.smartcity.common.exception.ParkingZoneCodeAlreadyTakenException;
import com.smartcity.common.exception.ParkingZoneNotFoundException;
import com.smartcity.parking.dto.request.ParkingZoneCreateRequest;
import com.smartcity.parking.dto.request.ParkingZoneUpdateRequest;
import com.smartcity.parking.dto.response.ParkingZoneDetailsResponse;
import com.smartcity.parking.dto.response.ParkingZoneResponse;
import com.smartcity.parking.entity.ParkingZone;
import com.smartcity.parking.mapper.ParkingZoneMapper;
import com.smartcity.parking.repository.ParkingZoneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneService {

    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingZoneMapper parkingZoneMapper;

    private ParkingZone getByZoneCodeOrByIdOrThrow(String zoneCode, Long zoneId, boolean byId) {
        return byId
                ? parkingZoneRepository.findById(zoneId).orElseThrow(
                        () -> new ParkingZoneNotFoundException("Zona de parcare nu a fost gasita pentru id-ul: " + zoneId)
                )
                : parkingZoneRepository.findByZoneCode(zoneCode).orElseThrow(
                        () -> new ParkingZoneNotFoundException("Zona de parcare nu a fost gasita pentru codul: " + zoneCode)
                );
    }

    public Page<ParkingZoneResponse> findAll(Pageable pageable) {
        return parkingZoneRepository
                .findAll(pageable)
                .map(parkingZoneMapper::parkingZoneToParkingZoneResponse);
    }

    public ParkingZoneDetailsResponse findByZoneCode(String zoneCode) {
        ParkingZone parkingZone = getByZoneCodeOrByIdOrThrow(zoneCode, null, false);
        return parkingZoneMapper.parkingZoneToParkingZoneDetailsResponse(parkingZone);
    }

    @Transactional
    public ParkingZoneDetailsResponse addParkingZone(ParkingZoneCreateRequest req) {
        ParkingZone parkingZone = parkingZoneMapper.parkingZoneCreateRequestToParkingZone(req);


        if (parkingZoneRepository.existsByZoneCode(req.zoneCode()))
            throw new ParkingZoneCodeAlreadyTakenException("Codul '" + req.zoneCode() +
                    "' este deja folosit de o alta zona de parcare.");

        ParkingZone savedParkingZone = parkingZoneRepository.save(parkingZone);
        return parkingZoneMapper.parkingZoneToParkingZoneDetailsResponse(savedParkingZone);
    }

    @Transactional
    public ParkingZoneDetailsResponse updateParkingZone(Long zoneId, ParkingZoneUpdateRequest req) {

        ParkingZone parkingZone = getByZoneCodeOrByIdOrThrow(null, zoneId, true);

        parkingZone.setZoneCode(req.zoneCode());
        parkingZone.setTariffPerHour(req.tariffPerHour());
        parkingZone.setTariffPerDay(req.tariffPerDay());
        parkingZone.setStatus(req.status());

        ParkingZone updatedParkingZone = parkingZoneRepository.save(parkingZone);

        return parkingZoneMapper.parkingZoneToParkingZoneDetailsResponse(updatedParkingZone);
    }

    @Transactional
    public void deleteParkingZone(Long id) {
        getByZoneCodeOrByIdOrThrow(null, id, true);
        parkingZoneRepository.deleteById(id);
    }
}
