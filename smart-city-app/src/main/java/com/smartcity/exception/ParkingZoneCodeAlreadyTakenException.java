package com.smartcity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParkingZoneCodeAlreadyTakenException extends RuntimeException {
    public ParkingZoneCodeAlreadyTakenException(String message) {
        super(message);
    }
}
