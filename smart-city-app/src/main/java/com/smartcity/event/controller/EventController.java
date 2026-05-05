package com.smartcity.event.controller;

import com.smartcity.common.api.ApiResponse;

import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.request.EventUpdateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.dto.response.EventResponse;
import com.smartcity.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/current-week")
    public ApiResponse<Page<EventResponse>> currentWeek(Pageable pageable) {
        return ApiResponse.success(
                eventService.findWithinWeek(0, pageable),
                "Evenimente curente."
        );
    }

    @GetMapping("/next-week")
    public ApiResponse<Page<EventResponse>> nextWeek(Pageable pageable) {
        return ApiResponse.success(
                eventService.findWithinWeek(1, pageable),
                "Evenimente pentru saptamana urmatoare."
        );
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailsResponse> eventDetails(@PathVariable Long eventId) {
        return ApiResponse.success(
                eventService.getEventDetails(eventId),
                "Detalii eveniment."
        );
    }

    @PostMapping("/")
    public ApiResponse<EventDetailsResponse> addEvent(@Valid @RequestBody EventCreateRequest req) {
        return ApiResponse.success(
                eventService.addEvent(req),
                "Evenimentul a fost adaugat cu succes."
        );
    }

    @PutMapping("/{eventId}")
    public ApiResponse<EventDetailsResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequest req
    ) {
        return ApiResponse.success(
                eventService.updateEvent(eventId, req),
                "Evenimentul a fost actualizat cu succes"
        );
    }

    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ApiResponse.successMessage("Evenimentul a fost sters cu succes.");
    }
}
