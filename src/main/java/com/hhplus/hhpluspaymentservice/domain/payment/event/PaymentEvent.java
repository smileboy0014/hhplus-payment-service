package com.hhplus.hhpluspaymentservice.domain.payment.event;

import com.hhplus.hhpluspaymentservice.domain.outbox.Outbox;
import com.hhplus.hhpluspaymentservice.domain.outbox.command.OutboxCommand;
import com.hhplus.hhpluspaymentservice.domain.payment.Payment;
import com.hhplus.hhpluspaymentservice.support.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@Setter
@ToString
public class PaymentEvent extends ApplicationEvent {

    public PaymentEvent() {
        super("");
    }


    private String reservationId;
    private String messageId;
    private Payment payment;
    private String token;

    public PaymentEvent(Object source, String reservationId,
                        Payment payment, String token) {
        super(source);
        this.reservationId = reservationId;
        this.payment = payment;
        this.token = token;
    }

    public OutboxCommand.Create toOutboxPaymentCommand() {
        String uuid = UUID.randomUUID().toString();
        this.messageId = uuid;
        return new OutboxCommand.Create(uuid, Outbox.DomainType.PAYMENT, Outbox.EventStatus.INIT, JsonUtils.toJson(this));
    }
}