package com.booking.ticketBooking.kafka.outboxPublisher;


import com.booking.ticketBooking.kafka.dto.OutboxEvent;
import com.booking.ticketBooking.kafka.repository.OutboxRepository;
import com.booking.ticketBooking.kafka.utils.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class OutboxPublisher {

private final OutboxRepository outboxRepository;

private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OutboxPublisher(OutboxRepository outboxRepository, @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 1000)
    public void pendingOutboxEvent(){
        List<OutboxEvent> remaingEventList = outboxRepository.findOutboxEventByStatus();

        for(OutboxEvent outboxEvent : remaingEventList){

            try{
                kafkaTemplate.executeInTransaction(
                        tx -> {

                            try {

                                Message<String> message = MessageBuilder.withPayload(outboxEvent.getPayload())
                                                            .setHeader(KafkaHeaders.TOPIC, outboxEvent.getTopicName())
                                                                    .setHeader(KafkaHeaders.KEY, outboxEvent.getEventId())
                                        .setHeader("outboxId", outboxEvent.getOutboxId())
                                                                            .build();
                                tx.send(message).get();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }

                            return true;
                        }


                );

                markPublished(outboxEvent);

            } catch (Exception e) {
                log.error("The outbox event send fail");
                throw new RuntimeException(e);
            }
        }
    }

    private void markPublished(OutboxEvent outboxEvent) {
        outboxEvent.setOutboxStatus(OutboxStatus.PUBLISHED);
        outboxRepository.save(outboxEvent);
    }

}
