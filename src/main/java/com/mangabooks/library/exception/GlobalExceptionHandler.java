package com.mangabooks.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    @ExceptionHandler(AuthorException.class)
    public ResponseEntity<ApiError> handleMissingAttributes(AuthorException ex,
                                                            HttpServletRequest request){
        ApiError error = new ApiError(
                LocalDateTime.now()
                , HttpStatus.UNPROCESSABLE_CONTENT.value()
                ,"Missing Authors"
                ,ex.getMessage()
                ,request.getRequestURI());
        return ResponseEntity.unprocessableContent().body(error);
    }
}
