package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.EmailSendDTO;
import com.example.capstoneback.Service.GmailMailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GmailMailSendController {

    private final GmailMailSenderService mailSender;

    @PostMapping("/mails/send")
    public ResponseEntity<String> sendEmail(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody EmailSendDTO emailSendDTO) {
        try {
            // "Bearer ~~~" → token String 넘김
            String token = (tokenHeader != null && tokenHeader.startsWith("Bearer "))
                    ? tokenHeader.substring(7)
                    : null;

            if (token == null) {
                return ResponseEntity.badRequest().body("Authorization 헤더가 잘못되었습니다.");
            }

            mailSender.sendEmailWithAccessToken(
                    token,
                    emailSendDTO.getToEmail(),
                    emailSendDTO.getSubject(),
                    emailSendDTO.getBody()
            );

            return ResponseEntity.ok("이메일 전송 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("이메일 전송 실패: " + e.getMessage());
        }
    }
}
