package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.EmailSendDTO;
import com.example.capstoneback.DTO.MailRequestDTO;
import com.example.capstoneback.Service.GmailMailSenderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GmailMailSendController {

    private final GmailMailSenderService mailSender;

//    @PostMapping("/mails/send")
//    public ResponseEntity<String> sendEmail(Authentication authentication,
//                                            @RequestBody EmailSendDTO emailSendDTO) {
//        System.out.println(authentication.toString());
//        mailSender.sendEmail(
//                authentication,
//                emailSendDTO.getToEmail(),
//                emailSendDTO.getSubject(),
//                emailSendDTO.getBody()
//        );
//        return ResponseEntity.ok("이메일 전송 성공");
//    }

    @PostMapping(value = "/mails/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmail(
            Authentication authentication,
            @RequestPart("data") MailRequestDTO mailRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            for (MailRequestDTO.EmailValue emailValue : mailRequestDTO.getToEmail()) {
                String to = emailValue.getValue();
                mailSender.sendEmail(authentication, to, mailRequestDTO.getSubject(), mailRequestDTO.getBody(), file);
            }
            return ResponseEntity.ok("메일 전송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메일 전송 실패: " + e.getMessage());
        }
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
