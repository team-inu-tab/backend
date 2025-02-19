package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class UserDoesntExistException extends RuntimeException {

    private ErrorCode errorCode;

    public UserDoesntExistException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
