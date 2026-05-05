package com.smartcity.event.mapper;

import com.smartcity.event.dto.request.EventCreateRequest;
import com.smartcity.event.dto.request.EventUpdateRequest;
import com.smartcity.event.dto.response.EventDetailsResponse;
import com.smartcity.event.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    EventDetailsResponse eventToEventDetailsResponse(Event event);
    Event eventCreateRequestToEvent(EventCreateRequest eventCreateRequest);
    Event eventUpdateRequestToEvent(EventUpdateRequest eventUpdateRequest);
}
