package com.hhplus.hhpluspaymentservice.infra.outbox;

import com.hhplus.hhpluspaymentservice.domain.outbox.Outbox;
import com.hhplus.hhpluspaymentservice.domain.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Optional<Outbox> getOutbox(String messageId) {
        Optional<OutboxEntity> outboxEntity = outboxJpaRepository.findByMessageId(messageId);
        if (outboxEntity.isPresent()) {
            return outboxEntity.map(OutboxEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public List<Outbox> getOutboxes() {
        return outboxJpaRepository.findAll()
                .stream()
                .map(OutboxEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Outbox> saveOutbox(Outbox outbox) {
        OutboxEntity outboxEntity = outboxJpaRepository.save(OutboxEntity.from(outbox));
        return Optional.of(outboxEntity.toDomain());

    }

    @Override
    public List<Outbox> getRetryOutboxes() {

        return outboxJpaRepository.findByStatusIs(Outbox.EventStatus.INIT).stream()
                .map(OutboxEntity::toDomain)
                .toList();

    }
}
