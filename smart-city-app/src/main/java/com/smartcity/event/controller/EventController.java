package com.smartcity.event.controller;

import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.request.EventUpdateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.dto.response.EventResponse;
import com.smartcity.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/current-week")
    public ResponseEntity<Page<EventResponse>> currentWeek(Pageable pageable) {
        return ResponseEntity.ok(eventService.findWithinWeek(0, pageable));
    }

    @GetMapping("/next-week")
    public ResponseEntity<Page<EventResponse>> nextWeek(Pageable pageable) {
        return ResponseEntity.ok(eventService.findWithinWeek(1, pageable));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsResponse> eventDetails(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventDetails(eventId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventDetailsResponse> addEvent(@Valid @ModelAttribute EventCreateRequest req) {
        return ResponseEntity.ok(eventService.addEvent(req));
    }

    @PutMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventDetailsResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @ModelAttribute EventUpdateRequest req
    ) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, req));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
