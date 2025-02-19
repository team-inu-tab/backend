package com.example.capstoneback.Error;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class NotValidArgumentException extends RuntimeException {
    private Errors errors;

    public NotValidArgumentException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }
}
