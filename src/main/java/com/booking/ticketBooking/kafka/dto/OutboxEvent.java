package com.booking.ticketBooking.kafka.dto;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.kafka.utils.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
public class OutboxEvent
{
    @Id
    @Column(updatable = false, nullable = false)
    private String outboxId;

    private String payload;

    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private String eventId;

    @Enumerated(value = EnumType.STRING)
    private OutboxStatus outboxStatus;

    private String eventType;

    private String topicName;

//    private boolean forceDLQ = false;
}
