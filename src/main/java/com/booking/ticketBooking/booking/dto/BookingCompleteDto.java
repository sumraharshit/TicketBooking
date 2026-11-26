package com.booking.ticketBooking.booking.dto;

import com.booking.ticketBooking.booking.utils.BookingStatus;

import java.util.UUID;


public record BookingCompleteDto (Long userId, String bookingId, Long ticketId, BookingStatus status) {


}
