package com.example.capstoneback.Error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler { // 전역 에러 처리 클래스

    //Validation 실패시 발생하는 에러 처리
    @ExceptionHandler(NotValidArgumentException.class)
    public ResponseEntity<Map<String ,Object>> handleNotValidArgumentException(NotValidArgumentException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        List<String> messages = new ArrayList<>();
        for(ObjectError error : e.getErrors().getAllErrors()){
            messages.add(error.getDefaultMessage());
        }
        errorResponse.put("message", messages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //요청 body에 타입 변환이 불가능한 값이 존재할 때 발생하는 에러 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String ,Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "타입에 맞는 올바른 값을 입력해주세요.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //유저를 찾지 못했을 때 발생하는 에러 처리
    @ExceptionHandler(UserDoesntExistException.class)
    public ResponseEntity<ErrorResponse> handleUserDoesntExistException(UserDoesntExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 파일 저장에 실패했을 때 발생하는 에러 처리
    @ExceptionHandler(FailedToSaveFileException.class)
    public ResponseEntity<ErrorResponse> handleFailedToSaveFileException(FailedToSaveFileException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<ErrorResponse> handleFileEmptyException(FileEmptyException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

}
