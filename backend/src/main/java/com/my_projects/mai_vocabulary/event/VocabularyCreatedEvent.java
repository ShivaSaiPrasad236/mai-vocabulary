package com.my_projects.mai_vocabulary.event;

import com.my_projects.mai_vocabulary.dto.VocabularyResponse;

public class VocabularyCreatedEvent {

    private final Long userId;
    private final String word;
    private final VocabularyResponse vocabulary;

    public VocabularyCreatedEvent(Long userId, String word, VocabularyResponse vocabulary) {

        this.userId = userId;
        this.word = word;
        this.vocabulary = vocabulary;
    }

    public Long getUserId() {
        return userId;
    }

    public String getWord() {
        return word;
    }

    public VocabularyResponse getVocabulary() {
        return vocabulary;
    }
}
