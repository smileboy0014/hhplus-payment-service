package com.hhplus.hhpluspaymentservice.domain.payment;


import com.hhplus.hhpluspaymentservice.domain.common.exception.CustomException;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_ALREADY_CANCEL_OR_REFUND;
import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_ALREADY_COMPLETE;


@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString
public class Payment implements Serializable {

    private Long paymentId;

    private Long reservationId;

    private BigDecimal paymentPrice;

    private PaymentStatus status; // 대기 / 완료 / 최소 / 환불

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    public enum PaymentStatus {
        PENDING, // 결제 진행 중
        COMPLETE, // 결제 완료
        CANCEL, // 결제 취소 완료
        REFUND // 결제 환불 완료

    }

    public void complete() {
        if (status == PaymentStatus.COMPLETE) {
            throw new CustomException(PAYMENT_ALREADY_COMPLETE,
                    "이미 결제되었습니다.");
        }
        status = PaymentStatus.COMPLETE;
    }

    public void fail() {
        status = PaymentStatus.CANCEL;
    }

    public void cancel() {
        if (status == PaymentStatus.COMPLETE) {
            status = PaymentStatus.REFUND;
        } else {
            throw new CustomException(PAYMENT_ALREADY_CANCEL_OR_REFUND,
                    "이미 취소됐거나 환불된 결제 정보입니다.");
        }
    }
}

