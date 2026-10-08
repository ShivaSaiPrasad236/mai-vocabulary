package com.my_projects.mai_vocabulary.event;

import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class VocabularyEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public VocabularyEventPublisher(ApplicationEventPublisher eventPublisher) {

        this.eventPublisher = eventPublisher;
    }

    public void publishVocabularyCreated(Long userId, String word, VocabularyResponse vocabulary) {

        eventPublisher.publishEvent(
                new VocabularyCreatedEvent(
                        userId,
                        word,
                        vocabulary
                )
        );
    }
}
