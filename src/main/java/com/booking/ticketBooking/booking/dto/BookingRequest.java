package com.booking.ticketBooking.booking.dto;

public class BookingRequest {

    private Long seatId;
    private String seatNumber;
    private Long userId;

    public Long getUserId(){
        return userId;
    }

    public String getSeatNumber(){
        return seatNumber;
    }

    public Long getSeatId(){
        return seatId;
    }
}
