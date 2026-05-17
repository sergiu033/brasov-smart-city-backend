package com.smartcity.event.mapper;

import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.entity.Event;
import com.smartcity.event.enums.EventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EventMapperImpl.class)
class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @Test
    void eventCreateRequestToEvent_mapsFields() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 18, 0);
        EventCreateRequest request = new EventCreateRequest(
                "Concert",
                "Descriere",
                "Piata Sfatului",
                start,
                start.plusHours(2),
                EventStatus.PLANNED,
                null);

        Event event = eventMapper.eventCreateRequestToEvent(request);

        assertThat(event.getTitle()).isEqualTo("Concert");
        assertThat(event.getDescription()).isEqualTo("Descriere");
        assertThat(event.getLocation()).isEqualTo("Piata Sfatului");
        assertThat(event.getStatus()).isEqualTo(EventStatus.PLANNED);
    }

    @Test
    void eventToEventDetailsResponse_mapsFields() {
        Event event = Event.builder()
                .id(1L)
                .title("Festival")
                .description("Detalii")
                .location("Brasov")
                .status(EventStatus.ONGOING)
                .imageUrl("2026/0601/img.jpg")
                .build();

        EventDetailsResponse response = eventMapper.eventToEventDetailsResponse(event);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Festival");
        assertThat(response.imageUrl()).isEqualTo("2026/0601/img.jpg");
    }
}
