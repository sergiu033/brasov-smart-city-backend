package com.smartcity.event.service;

import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.entity.Event;
import com.smartcity.event.enums.EventStatus;
import com.smartcity.event.mapper.EventMapper;
import com.smartcity.event.repository.EventRepository;
import com.smartcity.exception.EventNotFoundException;
import com.smartcity.imagestorage.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private EventService eventService;

    @Test
    void getEventDetails_returnsMappedEvent() {
        Event event = Event.builder().id(1L).title("Concert").build();
        EventDetailsResponse details = EventDetailsResponse.builder().id(1L).title("Concert").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.eventToEventDetailsResponse(event)).thenReturn(details);

        assertThat(eventService.getEventDetails(1L)).isEqualTo(details);
    }

    @Test
    void getEventDetails_throwsWhenMissing() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventDetails(99L))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void findWithinWeek_mapsEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        Event event = Event.builder().id(1L).title("T").location("L").build();
        when(eventRepository.findEventsByWeek(any(), any(), eq(null), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(event)));

        Page<com.smartcity.event.dto.response.EventResponse> page =
                eventService.findWithinWeek(0, "  ", pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().when()).isEqualTo("Saptamana curenta");
    }

    @Test
    void addEvent_savesWithoutImage() {
        EventCreateRequest request = new EventCreateRequest(
                "Festival", null, null, null, null, EventStatus.PLANNED, null);
        Event mapped = Event.builder().title("Festival").build();
        Event saved = Event.builder().id(1L).title("Festival").build();
        EventDetailsResponse details = EventDetailsResponse.builder().id(1L).title("Festival").build();

        when(eventMapper.eventCreateRequestToEvent(request)).thenReturn(mapped);
        when(eventRepository.save(mapped)).thenReturn(saved);
        when(eventMapper.eventToEventDetailsResponse(saved)).thenReturn(details);

        assertThat(eventService.addEvent(request)).isEqualTo(details);
    }

    @Test
    void deleteEvent_deletesById() {
        eventService.deleteEvent(5L);
        verify(eventRepository).deleteById(5L);
    }
}
