package com.booking.ticketBooking.kafka.repository;

import com.booking.ticketBooking.kafka.dto.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InboxRepository extends JpaRepository<InboxEvent, UUID> {
    InboxEvent findByBookingId(String bookingId);
}
