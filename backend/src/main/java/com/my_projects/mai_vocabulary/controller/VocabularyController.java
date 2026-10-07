package com.my_projects.mai_vocabulary.controller;

import com.my_projects.mai_vocabulary.dto.CreateVocabularyRequest;
import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import com.my_projects.mai_vocabulary.entity.VocabularyEntry;
import com.my_projects.mai_vocabulary.service.VocabularyService;
import com.sun.net.httpserver.HttpsServer;
import jakarta.validation.Valid;
import org.hibernate.annotations.NotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/vocabulary")
@CrossOrigin("*")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    @GetMapping("/{word}")
    public ResponseEntity<VocabularyResponse> getVocabularyEntry(@PathVariable String word){
        
        VocabularyEntry entry = vocabularyService.findByWord(word.trim().toLowerCase(Locale.ROOT));
        return new ResponseEntity<>(vocabularyService.toResponse(entry), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<VocabularyResponse>> getVocabularyEntries(){
        List<VocabularyEntry> entries = vocabularyService.getAllWords();
        if(entries != null)
            return new ResponseEntity<>(vocabularyService.toResponseList(entries), HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<?> createVocabularyEntry(@Valid @RequestBody CreateVocabularyRequest req) {

        VocabularyEntry entry = vocabularyService.createVocabularyEntry(req.getWord());
        return new ResponseEntity<>(vocabularyService.toResponse(entry), HttpStatus.CREATED);
    }
}
