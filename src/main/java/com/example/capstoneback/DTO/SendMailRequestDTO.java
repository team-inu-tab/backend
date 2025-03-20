package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;


//AWS SES 요구사항에 맞는 작성 폼
@Getter
@Builder
public class SendMailRequestDTO {
    private String from; // 발신자 메일 주소
    private String to; // 수신자 메일 주소
    private String subject; // 제목
    private String content; // 내용
}