package com.catalogue.controller.advice;

import com.catalogue.dto.ApiErrorResponse;
import com.catalogue.exceptions.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

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
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity itemNotFoundException(ItemNotFoundException itemNotFoundException) {
        log.error("No resource found exception occurred: {} ", itemNotFoundException.getMessage());
        HashMap<String, List<ApiErrorResponse>> errors = new HashMap<>();
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder().category(API_ERROR).status(HttpStatus.NO_CONTENT)
                .message(itemNotFoundException.getReason()).timestamp(LocalDateTime.now()).build();
        errors.put(ERRORS, List.of(apiErrorResponse));
        return new ResponseEntity(errors, itemNotFoundException.getStatusCode());
    }

}
