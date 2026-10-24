package com.booking.ticketBooking.ticket.dto;

import java.math.BigDecimal;

public record TicketDto(
 Long rowNumber,

 Long columnNumber,
 Long eventId,

 BigDecimal price

) {
}
