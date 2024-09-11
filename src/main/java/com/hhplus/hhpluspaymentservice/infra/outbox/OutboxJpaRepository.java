package com.hhplus.hhpluspaymentservice.infra.outbox;

import com.hhplus.hhpluspaymentservice.domain.outbox.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findByStatusIs(Outbox.EventStatus status);

    Optional<OutboxEntity> findByMessageId(String messageId);
}
