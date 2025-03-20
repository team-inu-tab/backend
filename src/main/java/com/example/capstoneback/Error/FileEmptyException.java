package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class FileEmptyException extends RuntimeException {

    private ErrorCode errorCode;

    public FileEmptyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
