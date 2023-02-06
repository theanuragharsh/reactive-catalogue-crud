package com.catalogue.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
    public BadRequestException(HttpStatus status, String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}