package com.my_projects.mai_vocabulary.exception;

import com.my_projects.mai_vocabulary.dto.ErrorResponseDto;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception){

        ErrorResponseDto errorResponse = new ErrorResponseDto("Something went wrong: " + exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(AIDefinitionException.class)
    public ResponseEntity<ErrorResponseDto> handleAIDefinitionException(AIDefinitionException exception){

        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid Request");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(message));
    }

    @ExceptionHandler(VocabularyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleVocabularyNotFoundException(VocabularyNotFoundException exception){
        ErrorResponseDto err = new ErrorResponseDto(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }
}
