package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class EmailDoesntExistException extends RuntimeException {

    private ErrorCode errorCode;

    public EmailDoesntExistException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
