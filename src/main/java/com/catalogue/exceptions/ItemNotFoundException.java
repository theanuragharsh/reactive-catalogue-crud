package com.catalogue.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {
    public ItemNotFoundException(HttpStatus status, String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
