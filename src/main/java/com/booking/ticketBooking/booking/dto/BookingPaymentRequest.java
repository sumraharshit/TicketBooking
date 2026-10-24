package com.booking.ticketBooking.booking.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record BookingPaymentRequest(UUID bookingId, LocalTime expiresAt) {
}
