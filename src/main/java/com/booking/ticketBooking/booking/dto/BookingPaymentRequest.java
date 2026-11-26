package com.booking.ticketBooking.booking.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record BookingPaymentRequest(String bookingId, LocalTime expiresAt) {
}
