package com.hhplus.hhpluspaymentservice.interfaces.consumer;

import com.hhplus.hhpluspaymentservice.domain.payment.PaymentService;
import com.hhplus.hhpluspaymentservice.domain.payment.event.PaymentEvent;
import com.hhplus.hhpluspaymentservice.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.hhplus.hhpluspaymentservice.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void handlePaymentEvent(String key, String message) {
        log.info("[KAFKA] :: CONSUMER:: Received PAYMENT_TOPIC, key: {}, payload: {}", key, message);

        PaymentEvent payload = JsonUtils.toObject(message, PaymentEvent.class);

        // 토큰 만료까지 완료되었을 경우
        if (payload != null && payload.getStatus().equals(PaymentEvent.EventConstants.TOKEN_EXPIRED)) {
            paymentService.completePayment(Long.valueOf(payload.getPaymentId()));
        }

        // 보상 트랜잭션을 시행해야하는 경우
        if (payload != null &&
                (payload.getStatus().equals(PaymentEvent.EventConstants.RESERVATION_FAILED) ||
                        payload.getStatus().equals(PaymentEvent.EventConstants.DEDUCTION_FAILED))) {
            paymentService.PaymentFallback(Long.valueOf(payload.getPaymentId()));
        }

//        ack.acknowledge(); //수동으로 offset 커밋
    }
}
