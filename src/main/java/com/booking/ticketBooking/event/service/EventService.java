package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.ticket.entity.TicketDto;

public interface EventService {

    void addEvent(EventDto dto);

    Event viewEvent(Long id);

    void addTickets(TicketDto ticketDto, Event event);
}
