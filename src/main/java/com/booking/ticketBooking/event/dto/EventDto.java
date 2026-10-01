package com.booking.ticketBooking.event.dto;

import com.booking.ticketBooking.event.entity.LineUp;
import com.booking.ticketBooking.event.entity.Venue;
import com.booking.ticketBooking.ticket.entity.Ticket;
import com.booking.ticketBooking.ticket.entity.TicketDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record EventDto(
      @Nullable Set<LineUpDto> lineUpsDto,
       @NotNull Long venueId,
       @NotNull String name,
      @Nullable TicketDto ticketsDto,
       @NotNull LocalDateTime eventDateTime,
       @NotNull @Column(length = 200) String description
){}
