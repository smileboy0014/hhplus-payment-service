package com.hhplus.hhpluspaymentservice.domain.payment.event;

import com.hhplus.hhpluspaymentservice.domain.outbox.Outbox;
import com.hhplus.hhpluspaymentservice.domain.outbox.command.OutboxCommand;
import com.hhplus.hhpluspaymentservice.support.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
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
    private String paymentId;
    private String userId;
    private String token;
    private BigDecimal amount;
    private String status;

    public PaymentEvent(Object source, String reservationId, String userId,
                        String paymentId, String token, BigDecimal amount, String status) {
        super(source);
        this.reservationId = reservationId;
        this.paymentId = paymentId;
        this.userId = userId;
        this.token = token;
        this.amount = amount;
        this.status = status;
    }

    public OutboxCommand.Create toOutboxCommand() {
        String uuid = UUID.randomUUID().toString();
        this.messageId = uuid;

        return new OutboxCommand.Create(uuid, Outbox.EventStatus.INIT, JsonUtils.toJson(this));
    }

    public static class EventConstants {
        public static final String NEW = "new";
        public static final String RESERVATION_COMPLETED = "reservation-completed";
        public static final String RESERVATION_FAILED = "reservation-failed";
        public static final String DEDUCTION_COMPLETED = "deduction-completed";
        public static final String DEDUCTION_FAILED = "deduction-failed";
        public static final String TOKEN_EXPIRED = "token-expired";
        public static final String TOKEN_FAILED = "token-failed";
    }
}