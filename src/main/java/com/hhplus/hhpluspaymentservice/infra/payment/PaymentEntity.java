package com.hhplus.hhpluspaymentservice.infra.payment;


import com.hhplus.hhpluspaymentservice.domain.payment.Payment;
import com.hhplus.hhpluspaymentservice.infra.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long reservationId;

    private BigDecimal paymentPrice;

    @Enumerated(EnumType.STRING)
    private Payment.PaymentStatus status; // 대기 / 완료 / 최소 / 환불

    private LocalDateTime paidAt; //결제가 완료됐을 때만

    @CreatedDate
    private LocalDateTime createdAt;

    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .paymentId(payment.getPaymentId() != null ? payment.getPaymentId() : null)
                .reservationId(payment.getReservationId())
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt() : null)
                .build();
    }

    public Payment toDomain() {
        return Payment
                .builder()
                .paymentId(paymentId)
                .reservationId(reservationId)
                .paymentPrice(paymentPrice)
                .status(status)
                .paidAt(paidAt)
                .createdAt(createdAt)
                .build();
    }

}

