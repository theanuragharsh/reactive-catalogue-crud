package com.catalogue.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DatabaseEmptyException extends ResponseStatusException {
    public DatabaseEmptyException(String reason) {
        super(HttpStatus.NO_CONTENT, reason);
    }
}
