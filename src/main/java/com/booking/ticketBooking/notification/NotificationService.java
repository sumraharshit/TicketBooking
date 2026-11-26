package com.booking.ticketBooking.notification;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.kafka.dto.InboxEvent;
import com.booking.ticketBooking.kafka.repository.InboxRepository;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.AcknowledgementCommitCallback;
import org.apache.kafka.clients.consumer.internals.AcknowledgementBatch;
import org.apache.kafka.clients.consumer.internals.Acknowledgements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.event.ObjectChangeListener;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {


    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;

    public NotificationService(ObjectMapper objectMapper, InboxRepository inboxRepository) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
    }


    @Transactional
    public void sendEmail(String payload, String outboxId) throws JsonProcessingException {


            BookingCompleteDto bookingCompleteDto = objectMapper.readValue(payload, BookingCompleteDto.class);

          InboxEvent inboxEvent = inboxRepository.findByBookingId(outboxId);
          if(inboxEvent != null){
              throw new IllegalArgumentException("The Event has already been published");
          }

            inboxEvent = new InboxEvent();
            inboxEvent.setOutboxId(outboxId);
            inboxEvent.setEventType("BOOKING-COMPLETED");
            inboxEvent.setInboxId(UUID.randomUUID().toString());

            inboxRepository.saveAndFlush(inboxEvent);


            log.info("The email has been sent with the following details: " + bookingCompleteDto.toString());



    }
}
