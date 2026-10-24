package com.booking.ticketBooking.booking.entity;

import com.booking.ticketBooking.booking.utils.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Setter
@Getter
public class BookingHistory {

    @Id
    private UUID id;

    private Long ticketId;

    private Long userId;

    private BigDecimal amount;

    private LocalDateTime bookedAt;

    private BookingStatus status;

    private LocalTime expiresAt;
}
