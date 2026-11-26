package com.booking.ticketBooking.kafka.repository;

import com.booking.ticketBooking.kafka.dto.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxRepository  extends JpaRepository<OutboxEvent, UUID>{
    List<OutboxEvent> findOutboxEventByStatus();

    Optional<OutboxEvent> findOutboxEventByOutboxId(UUID outboxId);

}
