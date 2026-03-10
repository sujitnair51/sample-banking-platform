package com.snbnk.documentation.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
public class OutboxEventEntity {

    @Id
    private UUID id;

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant createdAt;

    private boolean published;

    public static OutboxEventEntity create(String aggregateType,
                                       String aggregateId,
                                       String eventType,
                                       String payload) {

        OutboxEventEntity e = new OutboxEventEntity();
        e.id = UUID.randomUUID();
        e.aggregateType = aggregateType;
        e.aggregateId = aggregateId;
        e.eventType = eventType;
        e.payload = payload;
        e.createdAt = Instant.now();
        e.published = false;

        return e;
    }

    public void markPublished() {
        this.published = true;
    }
}
