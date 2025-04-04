package com.example.capstoneback.Error;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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

    // 파일 저장 시도할 때 파일이 비었을 경우 발생하는 에러 처리
    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<ErrorResponse> handleFileEmptyException(FileEmptyException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 이메일이 존재하지 않을 때 발생하는 에러 처리
    @ExceptionHandler(EmailDoesntExistException.class)
    public ResponseEntity<ErrorResponse> handleEmailDoesntExistException(EmailDoesntExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 파일이 존재하지 않을 때 발생하는 에러 처리
    @ExceptionHandler(FileDoesntExistException.class)
    public ResponseEntity<ErrorResponse> handleFileDoesntExistException(FileDoesntExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 사용자가 잘못된 접근을 할 때 발생하는 에러 처리
    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalAccessException(IllegalAccessException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 이미 존재하는 파일을 중복 저장하려 할 때 발생하는 에러 처리
    @ExceptionHandler(FileAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleFileAlreadyExistException(FileAlreadyExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // db에 없는 사용자로 email을 보낼 때 발생하는 에러 처리
    @ExceptionHandler(GmailSendFailedException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendFailedException(GmailSendFailedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    // 사용자가 타인의 gmail 데이터에 접근하려고 할 때 발생하는 에러 처리
    @ExceptionHandler(GoogleJsonResponseException.class)
    public ResponseEntity<Map<String, Object>> handleGoogleJsonResponseException(GoogleJsonResponseException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 사용자의 google oauth2 access token이 만료되었을 때 발생하는 에러 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
