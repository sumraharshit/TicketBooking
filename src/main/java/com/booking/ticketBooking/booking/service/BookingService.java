package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.seat.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final SeatService seatService;

    @Autowired
    BookingService(SeatService seatService){
        this.seatService = seatService;
    }

    public void createBooking(BookingRequest bookingRequest) throws Exception{
        log.info("Started Booking for the userId: " + bookingRequest.getUserId());
        seatService.seatLocking(bookingRequest);
        log.info("Booking successfully done for userId: " + bookingRequest.getUserId());
    }
}
