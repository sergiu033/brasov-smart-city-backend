package com.smartcity.event.controller;

import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.dto.response.EventResponse;
import com.smartcity.event.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    void currentWeek_delegatesToService() {
        Page<EventResponse> page = new PageImpl<>(List.of());
        PageRequest pageable = PageRequest.of(0, 10);
        when(eventService.findWithinWeek(0, "fest", pageable)).thenReturn(page);

        ResponseEntity<Page<EventResponse>> result = eventController.currentWeek("fest", pageable);

        assertThat(result.getBody()).isEqualTo(page);
    }

    @Test
    void eventDetails_delegatesToService() {
        EventDetailsResponse details = EventDetailsResponse.builder().id(1L).title("Concert").build();
        when(eventService.getEventDetails(1L)).thenReturn(details);

        assertThat(eventController.eventDetails(1L).getBody()).isEqualTo(details);
    }

    @Test
    void deleteEvent_returnsNoContent() {
        ResponseEntity<Void> result = eventController.deleteEvent(5L);

        verify(eventService).deleteEvent(5L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
