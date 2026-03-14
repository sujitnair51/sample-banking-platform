package com.snbnk.documentation.messaging;

import com.snbnk.documentation.persistence.OutboxEventEntity;
import com.snbnk.documentation.persistence.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String DOCUMENT_UPLOADED_TOPIC = "document-uploaded";
    private static final int MAX_RETRY = 5;

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publishUnpublishedEvents() {
        List<OutboxEventEntity> events =
                repository.findTop20ByPublishedFalseAndDeadLetterFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        log.info("Publishing {} outbox event(s)", events.size());

        for (OutboxEventEntity event : events) {
            try {
                String topic = resolveTopic(event);

//              Without .get(), send is async and your code might do this mark published = true -> Kafka send fails later
//              With .get(), send to Kafka -> wait for broker ack -> then mark published = true
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload()).get();

                event.markPublished();

                log.info("Published outbox event id={} type={} aggregateId={}",
                        event.getId(), event.getEventType(), event.getAggregateId());

            } catch (Exception ex) {
                String error = truncate(ex.getMessage(), 1000);

                if(event.getAttempts() >= MAX_RETRY) {
                    event.markDeadLetter(error);

                    log.error(
                            "OUTBOX EVENT MOVED TO DEAD LETTER id={} type={} attempts={} error={}",
                            event.getId(),
                            event.getEventType(),
                            event.getAttempts(),
                            error
                    );
                } else {
                    event.markFailed(error);

                    log.warn(
                            "Failed to publish outbox event id={} type={} attempts={} error={}",
                            event.getId(),
                            event.getEventType(),
                            event.getAttempts(),
                            error);
                }

            }
        }
    }

    private String resolveTopic(OutboxEventEntity event) {
        return switch (event.getEventType()) {
            case "DOCUMENT_UPLOADED" -> DOCUMENT_UPLOADED_TOPIC;
            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.getEventType());
        };
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
