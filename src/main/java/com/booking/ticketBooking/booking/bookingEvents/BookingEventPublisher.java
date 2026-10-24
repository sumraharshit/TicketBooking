package com.booking.ticketBooking.booking.bookingEvents;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BookingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void confirmedBookingEvent(BookingCompleteDto bookingCompleteDto){
        kafkaTemplate.send("booking.confirmed", bookingCompleteDto.bookingId().toString(), bookingCompleteDto);

        log.info("The successful booking confirmed event has been sent");

    }

}
