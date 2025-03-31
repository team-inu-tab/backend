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
    public ResponseEntity<String> sendEmail(Authentication authentication,
                                            @RequestBody EmailSendDTO emailSendDTO) {
        System.out.println(authentication.toString());
        mailSender.sendEmail(
                authentication,
                emailSendDTO.getToEmail(),
                emailSendDTO.getSubject(),
                emailSendDTO.getBody()
        );
        return ResponseEntity.ok("이메일 전송 성공");
    }

    @PostMapping("/mails/draft")
    public ResponseEntity<String> saveEmailToDraft(Authentication authentication,
                                                   @RequestBody EmailSendDTO emailSendDTO) {
        mailSender.saveEmailToDraft(
                authentication,
                emailSendDTO.getToEmail(),
                emailSendDTO.getSubject(),
                emailSendDTO.getBody()
        );
        return ResponseEntity.ok("메일 임시보관함 저장 성공");
    }

    @PatchMapping("/mails/draft/{draftId}")
    public ResponseEntity<String> updateDraftEmail(
            Authentication authentication,
            @PathVariable String draftId,
            @RequestBody EmailSendDTO emailSendDTO) {

        mailSender.updateDraftEmail(
                authentication,
                draftId,
                emailSendDTO.getToEmail(),
                emailSendDTO.getSubject(),
                emailSendDTO.getBody()
        );

        return ResponseEntity.ok("임시 메일 수정 성공");
    }

    @DeleteMapping("/mails/draft/{draftId}")
    public ResponseEntity<String> deleteDraftEmail(
            Authentication authentication,
            @PathVariable String draftId) {

        mailSender.deleteDraftEmail(authentication, draftId);

        return ResponseEntity.ok("임시 메일 삭제 성공");
    }

}
