package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class FailedToSaveFileException extends RuntimeException{

    private ErrorCode errorCode;

    public FailedToSaveFileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
