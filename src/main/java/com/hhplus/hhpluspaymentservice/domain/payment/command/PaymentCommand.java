package com.hhplus.hhpluspaymentservice.domain.payment.command;

import java.math.BigDecimal;

public class PaymentCommand {

    public record Create(
            Long reservationId,
            Long userId,
            String token,
            BigDecimal amount) {
    }

    public record Delete(
            Long reservationId) {
    }

}