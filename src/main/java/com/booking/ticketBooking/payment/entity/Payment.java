package com.booking.ticketBooking.payment.entity;

import com.booking.ticketBooking.payment.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private String orderId;

    @Column(updatable = false)
    private LocalDateTime paymentTime;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Integer amount;

    private String currency;

    private Long bookingId;

    private Long ticketId;

    private Long userId;

}
