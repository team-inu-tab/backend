package com.example.capstoneback.Error;

import lombok.Getter;

@Getter
public class GmailSendFailedException extends RuntimeException {

    private ErrorCode errorCode;

    public GmailSendFailedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = ErrorCode.USER_DOESNT_EXIST;
    }
}
