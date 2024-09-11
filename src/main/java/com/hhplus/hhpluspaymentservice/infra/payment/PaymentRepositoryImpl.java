package com.hhplus.hhpluspaymentservice.infra.payment;

import com.hhplus.hhpluspaymentservice.domain.payment.Payment;
import com.hhplus.hhpluspaymentservice.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return null;
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        paymentJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Payment> getPayment(Long reservationId) {
        Optional<PaymentEntity> paymentEntity = paymentJpaRepository.findByReservationId(reservationId);
        if (paymentEntity.isPresent()) {
            return paymentEntity.map(PaymentEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Payment> getCreatedPayment(Long paymentId) {
        Optional<PaymentEntity> paymentEntity = paymentJpaRepository.findById(paymentId);
        if (paymentEntity.isPresent()) {
            return paymentEntity.map(PaymentEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Payment> savePayment(Payment payment) {
        PaymentEntity paymentEntity = paymentJpaRepository.save(PaymentEntity.from(payment));

        return Optional.of(paymentEntity.toDomain());
    }

}
