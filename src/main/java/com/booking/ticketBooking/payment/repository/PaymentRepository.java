package com.booking.ticketBooking.payment.repository;

import com.booking.ticketBooking.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByIdempotencyKey(String idempotencyKey);

    Payment findByOrderId(String razorpayOrderId);
}
