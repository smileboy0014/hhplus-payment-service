package com.hhplus.hhpluspaymentservice.domain.payment;

import com.hhplus.hhpluspaymentservice.domain.common.exception.CustomException;
import com.hhplus.hhpluspaymentservice.domain.payment.command.PaymentCommand;
import com.hhplus.hhpluspaymentservice.domain.payment.event.PaymentEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_IS_FAILED;
import static com.hhplus.hhpluspaymentservice.domain.common.exception.ErrorCode.PAYMENT_IS_NOT_FOUND;
import static com.hhplus.hhpluspaymentservice.domain.payment.event.PaymentEvent.EventConstants.NEW;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {

        return paymentRepository.getCreatedPayment(paymentId).orElseThrow(()
                -> new CustomException(PAYMENT_IS_NOT_FOUND, PAYMENT_IS_NOT_FOUND.getMsg()));
    }

    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param command 결제 요청 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    @CircuitBreaker(name = "paymentEventPublisher", fallbackMethod = "fallbackPublishEvent")
    public Payment pay(PaymentCommand.Create command) {

        Payment payment = Payment.builder()
                .reservationId(command.reservationId())
                .paymentPrice(command.amount())
                .paidAt(now())
                .status(Payment.PaymentStatus.PENDING).build();

        // 1. 임시 결제 내역 생성
        Payment completePayment = paymentRepository.savePayment(payment).orElseThrow(
                () -> new CustomException(PAYMENT_IS_FAILED, "결제 완료 내역 생성에 실패하였습니다"));

        // 2. 임시 결제 완료 이벤트 발행
        publishPaymentEvent(command, completePayment);

        return completePayment;
    }

    @CircuitBreaker(name = "paymentEventPublisher", fallbackMethod = "fallbackPublishEvent")
    @Retry(name = "paymentEventPublisher")
    public void publishPaymentEvent(PaymentCommand.Create command, Payment completePayment) {
        publisher.publishEvent(new PaymentEvent(this, String.valueOf(command.reservationId()), String.valueOf(command.userId()),
                String.valueOf(completePayment.getPaymentId()), command.token(), command.amount(), NEW));
    }

    public void fallbackPublishEvent(PaymentCommand.Create command, Payment completePayment, Exception e) {
        // Fallback 로직을 여기서 처리, 예를 들어 로그를 남기거나 사용자에게 알림을 보낼 수 있음
        log.error("결제 이벤트 발행 실패: {}", e.getMessage());
    }

    @Transactional
    public void completePayment(Long paymentId) {

        Payment payment = paymentRepository.getCreatedPayment(paymentId)
                .orElseThrow(() -> new CustomException(PAYMENT_IS_NOT_FOUND, "결제 내역을 찾는데 실패하였습니다"));
        payment.complete();

        paymentRepository.savePayment(payment);
    }

    @Transactional
    public void PaymentFallback(Long paymentId) {
        // 1. 결제 내역 조회
        Payment payment = paymentRepository.getCreatedPayment(paymentId)
                .orElseThrow(() -> new CustomException(PAYMENT_IS_NOT_FOUND, "결제 내역을 찾는데 실패하였습니다"));
        // 2. 결제 취소
        payment.fail();

        paymentRepository.savePayment(payment);
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
