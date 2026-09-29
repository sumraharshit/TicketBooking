package com.booking.ticketBooking.ticket.entity;

import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.util.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//@Entity
//@Table(name="ticket")
//@Getter
//@Setter
public class Ticket {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(length = 1)
//    private Character row;
//
//    @Column(length = 2)
//    private Long seatNumber;
//
//    private Long userId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "event_id")
//    private Event event;
//
//    @Enumerated(value = EnumType.STRING)
//    private TicketStatus status = TicketStatus.AVAILABLE;
//
//    private LocalDateTime bookedAt;
}
