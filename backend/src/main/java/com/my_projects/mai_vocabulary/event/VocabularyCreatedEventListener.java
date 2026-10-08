package com.my_projects.mai_vocabulary.event;

import com.my_projects.mai_vocabulary.config.AsyncExceptionHandler;
import com.my_projects.mai_vocabulary.service.VocabularyCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class VocabularyCreatedEventListener {

    private final VocabularyCacheService vocabularyCacheService;

    private static final Logger logger =
            LoggerFactory.getLogger(
                    VocabularyCreatedEventListener.class
            );

    public VocabularyCreatedEventListener(
            VocabularyCacheService vocabularyCacheService) {

        this.vocabularyCacheService = vocabularyCacheService;
    }

    @Async("vocabularyTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleVocabularyCreated(VocabularyCreatedEvent event) {

        logger.info("Processing vocabulary event on thread: "
                + Thread.currentThread().getName());

        vocabularyCacheService.put(
                event.getUserId(),
                event.getWord(),
                event.getVocabulary()
        );
    }
}
