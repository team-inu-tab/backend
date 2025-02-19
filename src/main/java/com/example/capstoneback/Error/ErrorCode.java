package com.example.capstoneback.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode { //에러 정보를 정리해놓는 enum 클래스
    USER_DOESNT_EXIST(400, "user doesnt exist");

    private int status;
    private String message;
}
