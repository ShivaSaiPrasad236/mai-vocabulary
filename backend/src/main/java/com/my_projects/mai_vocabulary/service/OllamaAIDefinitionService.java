package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AIWordDefinitionResponse;
import com.my_projects.mai_vocabulary.exception.AIDefinitionException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaAIDefinitionService implements AIDefinitionService{

    private ChatClient chatClient;

    public OllamaAIDefinitionService(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AIWordDefinitionResponse getDefinition(String word) {
        String prompt = """
                You are a vocabulary assistant.

                Provide a clear and accurate definition for the English word: "%s".

                Include:
                - pronunciation
                - part of speech
                - meaning
                - one example sentence
                - synonyms
                - antonyms

                Rules:
                - Set "recognized" to true only if the input is a real English word with an established meaning.
                - Set "recognized" to false if the input is not a recognized English word.
                - If "recognized" is false, set pronunciation, part of speech, meaning, and example sentence to null.
                - If "recognized" is false, return empty arrays for synonyms and antonyms.
                - Return only information relevant to the given word.
                - Synonyms must be unique.
                - Antonyms must be unique.
                - Do not add leading or trailing spaces to any value.
                - Keep synonyms and antonyms concise.
                - Keep the meaning concise and suitable for someone learning English.
                """.formatted(word);

        try {
            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(AIWordDefinitionResponse.class);
        }
        catch (Exception exception){
            throw new AIDefinitionException(
                    "Unable to generate the vocabulary definition"
                    , exception
            );
        }
    }
}
