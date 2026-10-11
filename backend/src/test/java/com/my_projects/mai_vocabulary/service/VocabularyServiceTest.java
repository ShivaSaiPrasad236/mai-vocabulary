package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AIWordDefinitionResponse;
import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.entity.VocabularyEntry;
import com.my_projects.mai_vocabulary.event.VocabularyEventPublisher;
import com.my_projects.mai_vocabulary.exception.VocabularyNotFoundException;
import com.my_projects.mai_vocabulary.repository.VocabularyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private UserService userService;

    @Mock
    private AIDefinitionService aiDefinitionService;

    @Mock
    private VocabularyCacheService vocabularyCacheService;

    @Mock
    private VocabularyEventPublisher vocabularyEventPublisher;

    @InjectMocks
    private VocabularyService vocabularyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void shouldReturnExistingVocabularyWithoutCallingAI() {

        VocabularyEntry entry = new VocabularyEntry();
        entry.setId(10L);
        entry.setWord("abundant");
        entry.setUser(user);
        entry.setMeaning("Existing meaning");
        entry.setSynonyms(List.of("plentiful"));
        entry.setAntonyms(List.of("scarce"));

        when(userService.getCurrentUser()).thenReturn(user);

        when(vocabularyRepository.findByUserIdAndWord(
                1L,
                "abundant"
        )).thenReturn(Optional.of(entry));

        VocabularyResponse response =
                vocabularyService.findByWord("abundant");

        assertThat(response.getWord()).isEqualTo("abundant");
        assertThat(response.getMeaning()).isEqualTo("Existing meaning");

        verify(aiDefinitionService, never()).getDefinition(any());

        verify(vocabularyRepository)
                .findByUserIdAndWord(1L, "abundant");
    }

    @Test
    void shouldThrowExceptionWhenVocabularyDoesNotExist() {

        when(userService.getCurrentUser()).thenReturn(user);

        when(vocabularyRepository.findByUserIdAndWord(
                1L,
                "unknown"
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> vocabularyService.findByWord("unknown")
        )
                .isInstanceOf(VocabularyNotFoundException.class);
    }

    @Test
    void shouldUsePaginationWhenGettingAllWords() {

        VocabularyEntry entry = new VocabularyEntry();
        entry.setId(1L);
        entry.setWord("abundant");
        entry.setUser(user);
        entry.setSynonyms(List.of());
        entry.setAntonyms(List.of());

        Page<VocabularyEntry> page =
                new PageImpl<>(
                        List.of(entry),
                        PageRequest.of(0, 10),
                        1
                );

        when(userService.getCurrentUser()).thenReturn(user);

        when(vocabularyRepository.findAllByUserId(
                eq(1L),
                any(PageRequest.class)
        )).thenReturn(page);

        var response =
                vocabularyService.getAllWords(0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);

        verify(vocabularyRepository)
                .findAllByUserId(eq(1L), any(PageRequest.class));
    }

    @Test
    void shouldRejectInvalidPage() {

        assertThatThrownBy(
                () -> vocabularyService.getAllWords(-1, 10)
        )
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectInvalidPageSize() {

        assertThatThrownBy(
                () -> vocabularyService.getAllWords(0, 101)
        )
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldPublishVocabularyCreatedEventAfterSaving() {

        VocabularyEntry savedEntry = new VocabularyEntry();
        savedEntry.setId(20L);
        savedEntry.setWord("resilient");
        savedEntry.setUser(user);
        savedEntry.setMeaning("Able to recover quickly");
        savedEntry.setSynonyms(List.of("strong"));
        savedEntry.setAntonyms(List.of("fragile"));

        when(userService.getCurrentUser()).thenReturn(user);

        when(vocabularyRepository.findByUserIdAndWord(
                1L,
                "resilient"
        )).thenReturn(Optional.empty());

        AIWordDefinitionResponse aiResponse =
                new AIWordDefinitionResponse();

        aiResponse.setRecognized(true);
        aiResponse.setWord("resilient");
        aiResponse.setMeaning("Able to recover quickly");
        aiResponse.setSynonyms(List.of("strong"));
        aiResponse.setAntonyms(List.of("fragile"));

        when(aiDefinitionService.getDefinition("resilient"))
                .thenReturn(aiResponse);

        when(vocabularyRepository.save(any(VocabularyEntry.class)))
                .thenReturn(savedEntry);

        VocabularyEntry result =
                vocabularyService.createVocabularyEntry("resilient");

        assertThat(result).isEqualTo(savedEntry);

        verify(vocabularyRepository).save(any(VocabularyEntry.class));

        verify(vocabularyEventPublisher).publishVocabularyCreated(
                eq(1L),
                eq("resilient"),
                any(VocabularyResponse.class)
        );
    }
}
