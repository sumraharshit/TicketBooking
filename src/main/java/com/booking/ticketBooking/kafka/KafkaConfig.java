package com.booking.ticketBooking.kafka;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    @Bean
    public ProducerFactory<String, Object> transactionalProducerFactory(){
        Map<String, Object> prop;
        prop = mapProducerConfig();
        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(prop);
        pf.setTransactionIdPrefix("tx-");
        return pf;
    }

    @Bean
    public KafkaTemplate<String, Object> transactionalKafkaTemplate(@Qualifier("transactionalProducerFactory") ProducerFactory<String, Object> transactionalProducerFactory){
        return new KafkaTemplate<>(transactionalProducerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> nonTransactionalProducerFactory(){
        Map<String, Object> prop;
        prop = mapProducerConfig();

        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(prop);
        return pf;
    }

    @Bean(name = {"nonTransactionalKafkaTemplate", "kafkaTemplate"})
    public KafkaTemplate<String, Object> nonTransactionalKafkaTemplate(@Qualifier("nonTransactionalProducerFactory") ProducerFactory<String, Object> nonTransactionalProducerFactory){
        return new KafkaTemplate<>(nonTransactionalProducerFactory);
    }

    private Map<String, Object> mapProducerConfig(){
        Map<String, Object> prop = new HashMap<>();
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, String.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonProperties.Json.class);
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        prop.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        prop.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        prop.put(ProducerConfig.RETRIES_CONFIG, 3);
        prop.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        prop.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        prop.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        prop.put(ProducerConfig.ACKS_CONFIG,"all"); //acks=0; acks=1; acks=all

        return prop;
    }


    @Bean
    public NewTopic bookingConfirmed(){

    return TopicBuilder
            .name("booking.confirmed")
            .partitions(3)
            .build();
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
        return TopicBuilder
                .name("booking.failed")
                .partitions(3)
                .build();

    }


}
