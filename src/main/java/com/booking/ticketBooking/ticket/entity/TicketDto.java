package com.booking.ticketBooking.ticket.entity;

import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.util.TicketStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketDto(
 Long rowNumber,

 Long columnNumber,
 Long eventId,

 BigDecimal price

) {
}
