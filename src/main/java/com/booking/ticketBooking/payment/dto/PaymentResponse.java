package com.booking.ticketBooking.payment.dto;

import com.razorpay.Order;
import org.springframework.http.HttpStatus;

public record PaymentResponse(String msg, Order order, HttpStatus status) {
}
