package com.snbnk.documentation.messaging;

import com.snbnk.documentation.persistence.OutboxEventEntity;
import com.snbnk.documentation.persistence.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 2000)
    public void publish() {

        List<OutboxEventEntity> events =
                repository.findTop20ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEventEntity event : events) {

            kafkaTemplate.send(
                    "document-uploaded",
                    event.getAggregateId(),
                    event.getPayload()
            );

            event.markPublished();
        }
    }
}
