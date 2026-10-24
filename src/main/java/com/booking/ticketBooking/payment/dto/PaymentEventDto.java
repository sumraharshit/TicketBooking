package com.booking.ticketBooking.payment.dto;

import com.booking.ticketBooking.payment.utils.PaymentStatus;

public record PaymentEventDto(
        Long paymentId,
        String gatewayOrderId,
        PaymentStatus paymentStatus,
        Long paymentBookingId,
        Long paymentTicketId,
        Long paymentUserId
){}
