package com.hhplus.hhpluspaymentservice.domain.producer;

public interface EventProducer {

    void publish(String topic, String key, String payload);
}
