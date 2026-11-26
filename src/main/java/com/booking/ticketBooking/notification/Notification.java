package com.booking.ticketBooking.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class Notification {

    private final NotificationService notificationService;

    public Notification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = {"booking.confirmed", "booking.failed"}, groupId = "notification-booking")
    public void notification(@Payload String payload, @Header("outboxId") String outboxId, @Header(KafkaHeaders.RECEIVED_KEY) String eventId, Acknowledgment ack){

        try{
            notificationService.sendEmail(payload, outboxId);
        } catch (JsonProcessingException e){
            System.out.println("Cannot process the payload");
            throw new RuntimeException(e);
        } catch(DataIntegrityViolationException ex) {
            System.out.println("There was race condition while saving");

            ack.acknowledge();
        }

        ack.acknowledge();
    }
}
