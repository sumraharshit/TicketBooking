package com.booking.ticketBooking.booking.bookingEvents;

import com.booking.ticketBooking.booking.service.BookingService;
import com.booking.ticketBooking.payment.dto.PaymentEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private BookingService bookingService;

    public BookingEventListener(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "payment.successful", groupId = "payment-successful")
    public void paymentSuccessfulListener(PaymentEventDto paymentEventDto){
        log.info("The payment for successful booking is received");

        bookingService.successfulBooking(paymentEventDto.paymentBookingId(), paymentEventDto.paymentTicketId());

    }
}
