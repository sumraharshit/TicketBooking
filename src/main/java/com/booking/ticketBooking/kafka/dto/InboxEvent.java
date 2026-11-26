package com.booking.ticketBooking.kafka.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
public class InboxEvent {

    @Id
    private String inboxId;

    @Column(unique = true)
    private String outboxId;


    private String eventType;

}
