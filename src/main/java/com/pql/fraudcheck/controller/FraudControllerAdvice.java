package com.pql.fraudcheck.controller;

import com.pql.fraudcheck.util.LogHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@RestControllerAdvice
public class FraudControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex, HandlerMethod handlerMethod) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put("field", fieldName);
            errors.put("error", errorMessage);
        });

        LogHelper.logResult(handlerMethod.getMethod().getName(), false, "Validation error "+errors.toString());

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleMessageNotReadableExceptions(HttpMessageNotReadableException ex, HandlerMethod handlerMethod) {
        LogHelper.logResult(handlerMethod.getMethod().getName(), false, ex.getRootCause().getMessage());

        throw ex;
    }
}
