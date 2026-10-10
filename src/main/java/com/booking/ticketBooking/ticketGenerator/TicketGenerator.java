package com.booking.ticketBooking.ticketGenerator;

import com.booking.ticketBooking.booking.service.BookingCompleteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketGenerator {

    @KafkaListener(topics = "bookingTopic", groupId = "pdf-booking")
    public void consumeBookingInfo(BookingCompleteDto bookingCompleteDto){
        log.info("The ticket has been generated with the following details :" + bookingCompleteDto.toString());
    }
}
