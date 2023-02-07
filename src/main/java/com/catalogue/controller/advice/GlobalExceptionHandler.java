package com.catalogue.controller.advice;

import com.catalogue.dto.ApiErrorResponse;
import com.catalogue.exceptions.BadRequestException;
import com.catalogue.exceptions.DatabaseEmptyException;
import com.catalogue.exceptions.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    private static final String API_ERROR = "API_ERROR";
    private static final String MISSING_REQUIRED_PARAMETERS = "MISSING_REQUIRED_PARAMETERS";
    private static final String INVALID_VALUE = "INVALID_VALUE";
    private static final String ERRORS = "errors";

    @ExceptionHandler(value = ItemNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity itemNotFoundException(ItemNotFoundException itemNotFoundException) {
        log.error("No resource found exception occurred: {} ", itemNotFoundException.getMessage());
        HashMap<String, List<ApiErrorResponse>> errors = new HashMap<>();
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder().category(API_ERROR).status(HttpStatus.NOT_FOUND)
                .message(itemNotFoundException.getReason()).timestamp(LocalDateTime.now()).build();
        errors.put(ERRORS, List.of(apiErrorResponse));
        return new ResponseEntity(errors, itemNotFoundException.getStatusCode());
    }

    @ExceptionHandler(value = DatabaseEmptyException.class)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity databaseEmptyException(DatabaseEmptyException databaseEmptyException) {
        log.error("No resource found exception occurred: {} ", databaseEmptyException.getMessage());
        HashMap<String, List<ApiErrorResponse>> errors = new HashMap<>();
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder().category(API_ERROR).status(HttpStatus.NO_CONTENT)
                .message(databaseEmptyException.getReason()).timestamp(LocalDateTime.now()).build();
        errors.put(ERRORS, List.of(apiErrorResponse));
        return new ResponseEntity(errors, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity badRequestException(BadRequestException badRequestException) {
        log.error("BadRequestException occurred : {} ", badRequestException.getMessage());
        HashMap<String, List<ApiErrorResponse>> errors = new HashMap<>();
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder().category(API_ERROR).status(HttpStatus.BAD_REQUEST)
                .message(badRequestException.getReason()).timestamp(LocalDateTime.now()).build();
        errors.put(ERRORS, List.of(apiErrorResponse));
        return new ResponseEntity(errors, badRequestException.getStatusCode());
    }
}
