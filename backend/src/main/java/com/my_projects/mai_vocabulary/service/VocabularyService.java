package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AIWordDefinitionResponse;
import com.my_projects.mai_vocabulary.dto.PageResponse;
import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.entity.VocabularyEntry;
import com.my_projects.mai_vocabulary.exception.VocabularyNotFoundException;
import com.my_projects.mai_vocabulary.repository.VocabularyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final AIDefinitionService aiDefinitionService;
    private final UserService userService;
    private final VocabularyCacheService vocabularyCacheService;

    public VocabularyService(VocabularyRepository vocabularyRepository,
                             AIDefinitionService aiDefinitionService,
                             UserService userService,
                             VocabularyCacheService vocabularyCacheService){
        this.vocabularyRepository = vocabularyRepository;
        this.aiDefinitionService = aiDefinitionService;
        this.userService = userService;
        this.vocabularyCacheService = vocabularyCacheService;
    }

//    public List<VocabularyEntry> getAllWords(){
//
//        return vocabularyRepository.findAll();
//    }

    public PageResponse<VocabularyResponse> getAllWords(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        User user = userService.getCurrentUser();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "word"
                        )
                );

        Page<VocabularyEntry> vocabularyPage = vocabularyRepository.findAllByUserId(
                user.getId(), pageable
        );

        List<VocabularyResponse> responses =
                vocabularyPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new PageResponse<>(
                responses,
                vocabularyPage.getNumber(),
                vocabularyPage.getSize(),
                vocabularyPage.getTotalElements(),
                vocabularyPage.getTotalPages(),
                vocabularyPage.isFirst(),
                vocabularyPage.isLast()
        );
    }

    public VocabularyResponse findByWord(String word){

        String normalizedWord = word.trim().toLowerCase(Locale.ROOT);
        User user = userService.getCurrentUser();

        VocabularyResponse cachedEntry = vocabularyCacheService.get(user.getId(), normalizedWord);
        if(cachedEntry != null)
            return cachedEntry;

        VocabularyEntry entry = vocabularyRepository.findByUserIdAndWord(user.getId(), normalizedWord)
                .orElseThrow(() -> new VocabularyNotFoundException(
                                "Vocabulary entry not found for '" + normalizedWord + "'"
                        )
                );
        VocabularyResponse  entryReponse = toResponse(entry);
        vocabularyCacheService.put(user.getId(), normalizedWord, entryReponse);

        return entryReponse;
        //return vocabularyRepository.findByWord(word);
    }

//    public VocabularyEntry createVocabularyEntry(String word) {
//        String normalizedWord = word.trim().toLowerCase(Locale.ROOT);
//        VocabularyEntry exisingEntry = vocabularyRepository.findByWord(normalizedWord).orElse(null);
//        if (exisingEntry != null) {
//            return exisingEntry;
//        }
//
//        AIWordDefinitionResponse aiResponse = aiDefinitionService.getDefinition(normalizedWord);
//        if (!aiResponse.isRecognized()) {
//            throw new VocabularyNotFoundException(
//                    "The word '" + normalizedWord + "' was not recognized"
//            );
//        }
//        VocabularyEntry entry = new VocabularyEntry();
//        entry.setWord(normalizedWord);
//        entry.setPronunciation(clean(aiResponse.getPronunciation()));
//        entry.setPartOfSpeech(clean(aiResponse.getPartOfSpeech()));
//        entry.setMeaning(clean(aiResponse.getMeaning()));
//        entry.setExampleSentence(clean(aiResponse.getExampleSentence()));
//        entry.setSynonyms(cleanList(aiResponse.getSynonyms()));
//        entry.setAntonyms(cleanList(aiResponse.getAntonyms()));
//        entry.setCreatedAt(LocalDateTime.now());
//
//        return vocabularyRepository.save(entry);
//    }

    public VocabularyEntry createVocabularyEntry(String word) {

        String normalizedWord =
                word.trim().toLowerCase(Locale.ROOT);

        User user = userService.getCurrentUser();

        Optional<VocabularyEntry> existingEntry =
                vocabularyRepository.findByUserIdAndWord(
                        user.getId(),
                        normalizedWord
                );

        if (existingEntry.isPresent()) {
            vocabularyCacheService.put(user.getId(), normalizedWord, toResponse(existingEntry.get()));
            return existingEntry.get();
        }

        AIWordDefinitionResponse aiResponse =
                aiDefinitionService.getDefinition(normalizedWord);

        if (!aiResponse.isRecognized()) {
            throw new VocabularyNotFoundException(
                    "The word '" +
                            normalizedWord +
                            "' was not recognized"
            );
        }

        VocabularyEntry entry = new VocabularyEntry();

        entry.setUser(user);
        entry.setWord(normalizedWord);
        entry.setPronunciation(
                cleanValue(aiResponse.getPronunciation())
        );
        entry.setPartOfSpeech(
                cleanValue(aiResponse.getPartOfSpeech())
        );
        entry.setMeaning(
                cleanValue(aiResponse.getMeaning())
        );
        entry.setExampleSentence(
                cleanValue(aiResponse.getExampleSentence())
        );
        entry.setSynonyms(
                cleanList(aiResponse.getSynonyms())
        );
        entry.setAntonyms(
                cleanList(aiResponse.getAntonyms())
        );
        vocabularyCacheService.put(user.getId(), normalizedWord, toResponse(entry));
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
            return new ArrayList<>();

        return values
                .stream()
                .filter(Objects::nonNull)
                .map(v -> v.trim())
                .filter(v -> !v.isBlank())
                .distinct()
                .toList();
    }

    private String cleanValue(String value){
        if(value == null)
            return null;

        return value.trim();
    }
}
