package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AIWordDefinitionResponse;
import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import com.my_projects.mai_vocabulary.entity.VocabularyEntry;
import com.my_projects.mai_vocabulary.exception.VocabularyNotFoundException;
import com.my_projects.mai_vocabulary.repository.VocabularyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final AIDefinitionService aiDefinitionService;

    public VocabularyService(VocabularyRepository vocabularyRepository,
                             AIDefinitionService aiDefinitionService){
        this.vocabularyRepository = vocabularyRepository;
        this.aiDefinitionService = aiDefinitionService;
    }

    public List<VocabularyEntry> getAllWords(){

        return vocabularyRepository.findAll();
    }

    public VocabularyEntry findByWord(String word){

        String normalizedWord = word.trim().toLowerCase(Locale.ROOT);
        return vocabularyRepository.findByWord(normalizedWord)
                .orElseThrow(() ->
                        new VocabularyNotFoundException(
                                "Vocabulary entry not found for '" + normalizedWord + "'"
                        )
                );
        //return vocabularyRepository.findByWord(word);
    }

    public VocabularyEntry createVocabularyEntry(String word) {
        String normalizedWord = word.trim().toLowerCase(Locale.ROOT);
        VocabularyEntry exisingEntry = vocabularyRepository.findByWord(normalizedWord).orElse(null);
        if (exisingEntry != null) {
            return exisingEntry;
        }

        AIWordDefinitionResponse aiResponse = aiDefinitionService.getDefinition(normalizedWord);
        if (!aiResponse.isRecognized()) {
            throw new VocabularyNotFoundException(
                    "The word '" + normalizedWord + "' was not recognized"
            );
        }
        VocabularyEntry entry = new VocabularyEntry();
        entry.setWord(normalizedWord);
        entry.setPronunciation(clean(aiResponse.getPronunciation()));
        entry.setPartOfSpeech(clean(aiResponse.getPartOfSpeech()));
        entry.setMeaning(clean(aiResponse.getMeaning()));
        entry.setExampleSentence(clean(aiResponse.getExampleSentence()));
        entry.setSynonyms(cleanList(aiResponse.getSynonyms()));
        entry.setAntonyms(cleanList(aiResponse.getAntonyms()));
        entry.setCreatedAt(LocalDateTime.now());

        return vocabularyRepository.save(entry);
    }

    public VocabularyResponse toResponse(VocabularyEntry entry){

        VocabularyResponse response = new VocabularyResponse();
        response.setId(entry.getId());
        response.setWord(entry.getWord());
        response.setPronunciation(entry.getPronunciation());
        response.setPartOfSpeech(entry.getPartOfSpeech());
        response.setMeaning(entry.getMeaning());
        response.setExampleSentence(entry.getExampleSentence());
        response.setSynonyms(entry.getSynonyms());
        response.setAntonyms(entry.getAntonyms());

        return response;
    }

    public List<VocabularyResponse> toResponseList(List<VocabularyEntry> entries){

        List<VocabularyResponse> response = new ArrayList<>();
        for(VocabularyEntry e : entries){
            response.add(toResponse(e));
        }
        return response;
    }

    private List<String> cleanList(List<String> values){

        if(values == null)
            return List.of();

        return values
                .stream()
                .filter(Objects::nonNull)
                .map(v -> v.trim())
                .filter(v -> !v.isBlank())
                .distinct()
                .toList();
    }

    private String clean(String value){
        if(value == null)
            return null;

        return value.trim();
    }
}
