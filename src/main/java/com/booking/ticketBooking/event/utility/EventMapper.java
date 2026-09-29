package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);
}
