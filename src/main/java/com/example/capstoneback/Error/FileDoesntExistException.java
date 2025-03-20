package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class FileDoesntExistException extends RuntimeException {

    private ErrorCode errorCode;

    public FileDoesntExistException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
