package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.*;
import com.example.capstoneback.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/mails/receive")
    public ResponseEntity<List<ReceivedEmailResponseDTO>> getReceivedEmails(Authentication authentication){
        List<ReceivedEmailResponseDTO> responseDTO = emailService.getReceivedEmails(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/send")
    public ResponseEntity<List<SentEmailResponseDTO>> getSentEmails(Authentication authentication){
        List<SentEmailResponseDTO> responseDTO = emailService.getSentEmails(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/self")
    public ResponseEntity<List<SelfEmailResponseDTO>> getSelfEmail(Authentication authentication){
        List<SelfEmailResponseDTO> responseDTO = emailService.getSelfEmails(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/important")
    public ResponseEntity<List<ImportantEmailResponseDTO>> getImportantEmails(Authentication authentication){
        List<ImportantEmailResponseDTO> responseDTO = emailService.getImportantEmails(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/schedule")
    public ResponseEntity<List<ScheduledEmailResponseDTO>> getScheduledEmails(Authentication authentication){
        List<ScheduledEmailResponseDTO> responseDTO = emailService.getScheduledEmails(authentication);
        return ResponseEntity.ok(responseDTO);
    }
}
