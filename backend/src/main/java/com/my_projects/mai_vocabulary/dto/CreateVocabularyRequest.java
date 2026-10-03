package com.my_projects.mai_vocabulary.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateVocabularyRequest {


    @NotBlank(message = "Word cannot be empty")
    private String word;

    public String getWord(){
        return word;
    }

    public void setWord(String word){
        this.word = word;
    }


}
