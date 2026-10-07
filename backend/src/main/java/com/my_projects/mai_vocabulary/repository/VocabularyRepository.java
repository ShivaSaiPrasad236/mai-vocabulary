package com.my_projects.mai_vocabulary.repository;

import com.my_projects.mai_vocabulary.entity.VocabularyEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<VocabularyEntry, Long> {

    Optional<VocabularyEntry> findByWord(String word);

    Optional<VocabularyEntry> findByUserIdAndWord(
            Long userId,
            String word
    );

    Page<VocabularyEntry> findAllByUserId(Long userId, Pageable pageable);
}


