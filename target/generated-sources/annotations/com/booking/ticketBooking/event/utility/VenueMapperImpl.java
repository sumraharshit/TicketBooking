package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.VenueDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.event.entity.Venue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-01T18:51:11+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class VenueMapperImpl implements VenueMapper {

    @Override
    public Venue toEntity(VenueDto venueDto) {
        if ( venueDto == null ) {
            return null;
        }

        Venue venue = new Venue();

        List<Event> list = venueDto.events();
        if ( list != null ) {
            venue.setEvents( new ArrayList<Event>( list ) );
        }
        venue.setName( venueDto.name() );
        venue.setLocalAdress( venueDto.localAdress() );
        venue.setCity( venueDto.city() );
        venue.setState( venueDto.state() );

        return venue;
    }

    @Override
    public VenueDto toDto(Venue venue) {
        if ( venue == null ) {
            return null;
        }

        List<Event> events = null;
        String name = null;
        String localAdress = null;
        String city = null;
        String state = null;

        List<Event> list = venue.getEvents();
        if ( list != null ) {
            events = new ArrayList<Event>( list );
        }
        name = venue.getName();
        localAdress = venue.getLocalAdress();
        city = venue.getCity();
        state = venue.getState();

        VenueDto venueDto = new VenueDto( events, name, localAdress, city, state );

        return venueDto;
    }
}
