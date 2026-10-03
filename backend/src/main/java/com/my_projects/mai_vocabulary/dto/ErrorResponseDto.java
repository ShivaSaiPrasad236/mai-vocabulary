package com.my_projects.mai_vocabulary.dto;

public class ErrorResponseDto {

    private String message;

    public ErrorResponseDto(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
