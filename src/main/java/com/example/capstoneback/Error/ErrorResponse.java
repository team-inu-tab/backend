package com.example.capstoneback.Error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse { //에러 정보를 반환하는 응답 전용 타입 클래스
    private int status;
    private String message;

    public ErrorResponse(ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
