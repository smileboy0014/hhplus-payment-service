package com.hhplus.hhpluspaymentservice.support.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.hhplus.hhpluspaymentservice.support.config.KafkaTopicConfig.KafkaConstants.*;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics newTopics() {
        return new KafkaAdmin.NewTopics(
                Stream.of(PAYMENT_TOPIC, CONCERT_TOPIC, USER_TOPIC, QUEUE_TOPIC)
                        .map(topic -> new NewTopic(topic, 3, (short) 1))
                        .toArray(NewTopic[]::new)
        );
    }

    public static class KafkaConstants {
        public static final String PAYMENT_TOPIC = "payment-events";
        public static final String CONCERT_TOPIC = "concert-events";
        public static final String USER_TOPIC = "user-events";
        public static final String QUEUE_TOPIC = "queue-events";
    }
}
