package com.hhplus.hhpluspaymentservice.domain.outbox.command;

import com.hhplus.hhpluspaymentservice.domain.outbox.Outbox;

public class OutboxCommand {
    public record Create(
            String messageId,
            Outbox.DomainType type,
            Outbox.EventStatus status,
            String payload) {

        public Outbox toDomain() {
            return Outbox.builder()
                    .messageId(messageId)
                    .type(type)
                    .status(status)
                    .payload(payload)
                    .build();
        }
    }
}
