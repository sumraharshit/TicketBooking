package com.booking.ticketBooking.booking.dto;

import com.booking.ticketBooking.booking.utils.BookingStatus;

import java.util.UUID;


public record BookingCompleteDto (Long userId, UUID bookingId, Long ticketId, BookingStatus status) {


}
