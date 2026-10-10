package com.booking.ticketBooking.booking.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record BookingCompleteDto (Long userId, Long seatNumber, Character row, BigDecimal price, String eventName, String venueName, LocalDateTime eventDateTime) {


}
