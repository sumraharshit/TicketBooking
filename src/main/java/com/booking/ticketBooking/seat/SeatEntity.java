package com.booking.ticketBooking.seat;

import com.booking.ticketBooking.util.SeatStatus;
import com.booking.ticketBooking.util.SeatType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Seat")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Generated Values, having the strategy of the increment by 1
    private Long seatId;

    private String seatNumber;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Column(nullable = true) //the @Column(nullable=true) let me tell the jpa to tell the database that at the time of the creation this coloumn can be nulll
    private Long userId;

    public SeatStatus getSeatStatus(){
        return status;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setStatus(SeatStatus status){
        this.status = status;
    }

}
