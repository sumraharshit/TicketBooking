package com.booking.ticketBooking.notification;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @KafkaListener(topics = {"booking.confirmed", "booking.failed"}, groupId = "notification-booking")
    public void sendEmail(BookingCompleteDto bookingCompleteDto){

        log.info("The email has been sent with the following details: " + bookingCompleteDto.toString());

    }
}
