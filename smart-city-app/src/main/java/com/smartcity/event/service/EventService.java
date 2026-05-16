package com.smartcity.event.service;

import com.smartcity.exception.EventNotFoundException;
import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.request.EventUpdateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.dto.response.EventResponse;
import com.smartcity.event.entity.Event;
import com.smartcity.event.mapper.EventMapper;
import com.smartcity.event.repository.EventRepository;
import com.smartcity.imageservice.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ImageStorageService imageStorageService;

    private Event getByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Evenimentul nu a fost gasit")
        );
    }

    private Pair<LocalDateTime, LocalDateTime> getWeekBounds(int weekOffset) {

        LocalDate today = LocalDate.now();
        LocalDate weekStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(weekOffset);
        LocalDate nextWeekStartDate = weekStartDate.plusWeeks(1);

        LocalDateTime weekStart = weekStartDate.atStartOfDay();
        LocalDateTime nextWeekStart = nextWeekStartDate.atStartOfDay();

        return Pair.of(weekStart, nextWeekStart);
    }

    public Page<EventResponse> findWithinWeek(int weekOffset, Pageable pageable) {

        Pair<LocalDateTime, LocalDateTime> weekBounds = getWeekBounds(weekOffset);
        Page<Event> events = eventRepository.findEventsByWeek(weekBounds.getFirst(), weekBounds.getSecond(), pageable);

        return events.map(
                e -> EventResponse.builder()
                        .id(e.getId())
                        .title(e.getTitle())
                        .when(weekOffset == 0 ? "Saptamana curenta" : "Saptamana viitoare")
                        .location(e.getLocation())
                        .build()
        );
    }

    public EventDetailsResponse getEventDetails(Long eventId) {
        Event event = getByIdOrThrow(eventId);
        return eventMapper.eventToEventDetailsResponse(event);
    }

    @Transactional
    public EventDetailsResponse addEvent(EventCreateRequest req) {

        String filePath = "";

        try {
            InputStream image = req.image().getInputStream();
            filePath = imageStorageService.saveImage(image, req.image().getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Event event = eventMapper.eventCreateRequestToEvent(req);
        event.setImageUrl(filePath);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.eventToEventDetailsResponse(savedEvent);
    }

    @Transactional
    public EventDetailsResponse updateEvent(Long id, EventUpdateRequest req) {

        Event event = getByIdOrThrow(id);

        event.setTitle(req.title());
        event.setStatus(req.status());
        event.setDescription(req.description());
        event.setLocation(req.location());
        event.setStartTime(req.startTime());
        event.setEndTime(req.endTime());

        Event updatedEvent = eventRepository.save(event);

        return eventMapper.eventToEventDetailsResponse(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = getByIdOrThrow(id);
        eventRepository.deleteById(id);
    }

}