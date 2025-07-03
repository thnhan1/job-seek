package com.nhanab.demosecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InfoAlreadyRegisteredException extends RuntimeException {
    public InfoAlreadyRegisteredException(String message) {
        super(message);
    }
    public InfoAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
