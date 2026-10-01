package com.booking.ticketBooking.ticket.entity;

import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.util.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_row", length = 1)
    private Character row;

    @Column(length = 2)
    private Long seatNumber;

    private Long userId;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(value = EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime bookedAt;
}
