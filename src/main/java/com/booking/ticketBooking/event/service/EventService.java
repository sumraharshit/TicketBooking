package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.EventDto;

public interface EventService {

    void addEvent(EventDto dto);

    void viewEvent(Long id);
}
