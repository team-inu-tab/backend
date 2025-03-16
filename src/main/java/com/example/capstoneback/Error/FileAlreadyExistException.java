package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class FileAlreadyExistException extends RuntimeException{

    private ErrorCode errorCode;

    public FileAlreadyExistException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
