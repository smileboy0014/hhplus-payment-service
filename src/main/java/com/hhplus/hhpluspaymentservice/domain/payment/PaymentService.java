package com.hhplus.hhpluspaymentservice.domain.payment;

import com.hhplus.hhpluspaymentservice.domain.common.exception.CustomException;
import com.hhplus.hhpluspaymentservice.domain.payment.command.PaymentCommand;
import com.hhplus.hhpluspaymentservice.domain.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_IS_FAILED;
import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_IS_NOT_FOUND;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventProducer publisher;

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {
        Optional<Payment> payment = paymentRepository.getCreatedPayment(paymentId);

        if (payment.isEmpty()) throw new CustomException(PAYMENT_IS_NOT_FOUND,
                PAYMENT_IS_NOT_FOUND.getMsg());

        return payment.get();

    }

    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param command 결제 요청 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    public Payment pay(PaymentCommand.Create command) {

        Payment payment = Payment.builder()
                .reservationId(command.reservationId())
                .paymentPrice(command.amount())
                .paidAt(now())
                .status(Payment.PaymentStatus.COMPLETE).build();

        // 1. 결제 내역 생성
        Optional<Payment> completePayment = paymentRepository.savePayment(payment);

        if (completePayment.isEmpty()) {
            throw new CustomException(PAYMENT_IS_FAILED, "결제 완료 내역 생성에 실패하였습니다");
        }
        // 2. 결제 완료 이벤트 발행
//        publisher.publish(PAYMENT_TOPIC, String.valueOf(completePayment.get().getPaymentId()),
//                JsonUtils.toJson(event));

        return completePayment.get();
    }

    @Transactional
    public Payment cancelPayment(PaymentCommand.Delete command) {
        // 1. 결제 내역 조회
        Optional<Payment> payment = paymentRepository.getPayment(command.reservationId());

        // 1-1. 환불 처리
        if (payment.isPresent()) {
            payment.get().cancel();
            paymentRepository.savePayment(payment.get());

            return payment.get();
        }

        // 1-2. 결제 취소 내역 생성
        Optional<Payment> cancelPayment = paymentRepository.savePayment(Payment.builder()
                .reservationId(command.reservationId())
                .status(Payment.PaymentStatus.CANCEL)
                .paymentPrice(BigDecimal.ZERO)
                .build());

        if (cancelPayment.isEmpty()) {
            throw new CustomException(PAYMENT_IS_FAILED, "결제 취소 내역 생성에 실패하였습니다");
        }

        return cancelPayment.get();
    }
}
