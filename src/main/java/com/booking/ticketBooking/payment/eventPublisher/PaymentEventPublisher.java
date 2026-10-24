package com.booking.ticketBooking.payment.eventPublisher;

import com.booking.ticketBooking.payment.entity.Payment;
import com.booking.ticketBooking.payment.dto.PaymentEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String KAFKA_SUCCESSFUL_PAYMENT_EVENT = "payment.successful";


    @Autowired
    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentSuccessfulEvent(Payment payment) {

        PaymentEventDto paymentEvent = new PaymentEventDto(
            payment.getId(),
        payment.getOrderId(),
        payment.getPaymentStatus(),
        payment.getBookingId(),
        payment.getTicketId(),
        payment.getUserId()
        );



        kafkaTemplate.send(KAFKA_SUCCESSFUL_PAYMENT_EVENT, paymentEvent);
        log.info("The payment.successful event successfully sent: " + paymentEvent);

    }
}
