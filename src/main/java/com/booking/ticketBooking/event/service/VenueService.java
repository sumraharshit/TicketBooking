package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.PerformerDto;
import com.booking.ticketBooking.event.dto.VenueDto;

public interface VenueService {

    VenueDto viewVenue(Long id);

    String addVenue(VenueDto venueDto);

    void deleteVenue(Long id);

}
