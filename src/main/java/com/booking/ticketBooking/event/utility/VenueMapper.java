package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.VenueDto;
import com.booking.ticketBooking.event.entity.Venue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    Venue toEntity(VenueDto venueDto);

    VenueDto toDto(Venue venue);
}
