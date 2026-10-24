package com.booking.ticketBooking.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic bookingConfirmed(){

    return TopicBuilder
            .name("booking.confirmed").build();
    }


    @Bean
    public NewTopic paymentSuccessfulTopic(){
        return TopicBuilder.name("payment.successful").build();
    }

    @Bean
    public NewTopic paymentFailureTopic(){
        return TopicBuilder.name("payment.failure").build();
    }

    @Bean
    public NewTopic bookingFailure(){
        return TopicBuilder.name("booking.failed").build();
    }


}
