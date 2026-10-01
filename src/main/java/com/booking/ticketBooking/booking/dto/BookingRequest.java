package com.booking.ticketBooking.booking.dto;

import lombok.Getter;

import java.util.List;


public record BookingRequest(Long eventId, Long userId, Long ticketId) {

}
