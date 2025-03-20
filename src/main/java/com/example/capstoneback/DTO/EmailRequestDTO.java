package com.example.capstoneback.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    private String subject;   // 이메일 제목
    private String sender;    // 발신자 이메일
    private String receiver;  // 수신자 이메일
    private String timestamp; // 이메일 송신 시간
    private String content;   // 이메일 본문
}
