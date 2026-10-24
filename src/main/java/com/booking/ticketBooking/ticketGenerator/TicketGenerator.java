package com.booking.ticketBooking.ticketGenerator;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketGenerator {

    @KafkaListener(topics = "booking.confirmed", groupId = "pdf-booking")
    public void consumeBookingInfo(BookingCompleteDto bookingCompleteDto){
        log.info("The ticket has been generated with the following details :" + bookingCompleteDto.toString());
    }
}
