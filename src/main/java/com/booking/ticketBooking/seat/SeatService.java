package com.booking.ticketBooking.seat;

import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.util.SeatStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    private static final Logger log = LoggerFactory.getLogger(SeatService.class);

    @Autowired
    SeatService(SeatRepository seatRepository){
        this.seatRepository = seatRepository;
    }

    @Transactional
    public void seatLocking(BookingRequest bookingRequest) throws Exception{

        SeatEntity seat = seatRepository.findBySeatNumber(bookingRequest.getSeatNumber()).orElseThrow(
                () -> new IllegalArgumentException("Seat Id is wrong")
        );

        if(seat.getSeatStatus() == SeatStatus.AVAILABLE){
//
//            seatRepository.seatLocking(bookingRequest.getSeatId(), bookingRequest.getUserId());
            seat.setStatus(SeatStatus.BOOKED);
            seat.setUserId(bookingRequest.getUserId());

            log.info("The seat has been booked for the user: " + bookingRequest.getUserId());
        }
        else {
            throw new Exception("The seat is not available for user: " + bookingRequest.getUserId());
        }


    }
}
