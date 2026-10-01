package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.dto.LineUpDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.ticket.entity.TicketDto;
import java.time.LocalDateTime;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-01T18:51:11+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEntity(EventDto eventDto) {
        if ( eventDto == null ) {
            return null;
        }

        Event event = new Event();

        event.setName( eventDto.name() );
        event.setEventDateTime( eventDto.eventDateTime() );
        event.setDescription( eventDto.description() );

        return event;
    }

    @Override
    public EventDto toDto(Event event) {
        if ( event == null ) {
            return null;
        }

        String name = null;
        LocalDateTime eventDateTime = null;
        String description = null;

        name = event.getName();
        eventDateTime = event.getEventDateTime();
        description = event.getDescription();

        Set<LineUpDto> lineUpsDto = null;
        Long venueId = null;
        TicketDto ticketsDto = null;

        EventDto eventDto = new EventDto( lineUpsDto, venueId, name, ticketsDto, eventDateTime, description );

        return eventDto;
    }
}
