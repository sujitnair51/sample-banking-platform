package com.snbnk.onboarding.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
@Getter
@NoArgsConstructor
public class ProcessedEventEntity {

    @Id
    private String eventId;

    private Instant processedAt;

    public static ProcessedEventEntity create(String eventId) {
        ProcessedEventEntity e = new ProcessedEventEntity();
        e.eventId = eventId;
        e.processedAt = Instant.now();
        return e;
    }
}
