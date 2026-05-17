package com.smartcity.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(error(message, request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(error(ex.getMessage(), request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error("Email sau parola incorecta.", request));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorDetails> handleLocked(LockedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error(ex.getMessage(), request));
    }

    @ExceptionHandler({ParkingZoneCodeAlreadyTakenException.class, RecommendationCategoryCodeAlreadyTaken.class})
    public ResponseEntity<ErrorDetails> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error(ex.getMessage(), request));
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            EventNotFoundException.class,
            ParkingZoneNotFoundException.class,
            RecommendationNotFoundException.class,
            RecommendationCategoryNotFoundException.class,
            ReportCategoryNotFoundException.class,
            UserNotFoundException.class,
            FileNotFoundException.class,
            VehicleNotFoundException.class
    })
    public ResponseEntity<ErrorDetails> handleNotFound(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(ex.getMessage(), request));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorDetails> handleSecurity(SecurityException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage(), request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(error(ex.getMessage(), request));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error(ex.getMessage(), request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleUnexpected(Exception ex, HttpServletRequest request) {
        String message = "Eroare interna la procesarea request-ului pentru " + request.getRequestURI();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(message, request));
    }

    private static ErrorDetails error(String message, HttpServletRequest request) {
        return ErrorDetails.builder()
                .message(message)
                .timestamp(OffsetDateTime.now())
                .details(request == null ? null : request.getRequestURI())
                .build();
    }
}
