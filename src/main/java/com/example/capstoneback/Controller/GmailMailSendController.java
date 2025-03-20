package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.EmailSendDTO;
import com.example.capstoneback.Service.GmailMailSenderService;
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
            Authentication authentication,
            @RequestBody EmailSendDTO emailSendDTO
    ) {
        System.out.println("mail send");
        mailSender.sendEmail(
                authentication,
                emailSendDTO.getToEmail(),
                emailSendDTO.getSubject(),
                emailSendDTO.getBody()
        );
        return ResponseEntity.ok("이메일 전송 성공");
    }
}
