package com.booking.ticketBooking.event.dto;

import com.booking.ticketBooking.ticket.dto.TicketDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record EventDto(
      @Nullable Set<LineUpDto> lineUpsDto,
       @NotNull Long venueId,
       @NotNull String name,
      @Nullable TicketDto ticketsDto,
       @NotNull LocalDateTime eventDateTime,
       @NotNull @Column(length = 200) String description
){}
