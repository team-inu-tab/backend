package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.*;
import com.example.capstoneback.Error.NotValidArgumentException;
import com.example.capstoneback.Service.EmailService;
import com.example.capstoneback.Service.GmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmailController {

//    private final EmailService emailService;
    private final GmailService gmailService;

    @GetMapping("/mails/receive")
    public ResponseEntity<List<ReceivedEmailResponseDTO>> getReceivedEmails(Authentication authentication) throws IOException {
        List<ReceivedEmailResponseDTO> responseDTO = gmailService.getReceivedGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/send")
    public ResponseEntity<List<SentEmailResponseDTO>> getSentEmails(Authentication authentication) throws IOException {
        List<SentEmailResponseDTO> responseDTO = gmailService.getSentGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/self")
    public ResponseEntity<List<SelfEmailResponseDTO>> getSelfEmail(Authentication authentication) throws IOException {
        List<SelfEmailResponseDTO> responseDTO = gmailService.getSelfGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/important")
    public ResponseEntity<List<ImportantEmailResponseDTO>> getImportantEmails(Authentication authentication) throws IOException {
        List<ImportantEmailResponseDTO> responseDTO = gmailService.getImportantGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

//    @GetMapping("/mails/schedule")
//    public ResponseEntity<List<ScheduledEmailResponseDTO>> getScheduledEmails(Authentication authentication){
//        List<ScheduledEmailResponseDTO> responseDTO = emailService.getScheduledEmails(authentication);
//        return ResponseEntity.ok(responseDTO);
//    }

    @GetMapping("/mails/draft")
    public ResponseEntity<List<DraftEmailResponseDTO>> getDraftEmails(Authentication authentication) throws IOException {
        List<DraftEmailResponseDTO> responseDTO = gmailService.getDraftGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

//    @GetMapping("/mails/{id}")
//    public ResponseEntity<MailDetailsResponseDTO> getEmailDetails(@PathVariable("id") Long mailId, Authentication authentication) throws IllegalAccessException {
//        MailDetailsResponseDTO responseDTO = emailService.getMailDetails(mailId ,authentication);
//        return ResponseEntity.ok(responseDTO);
//    }

    @GetMapping("/mails/spam")
    public ResponseEntity<List<SpamEmailResponseDTO>> getSpamEmails(Authentication authentication) throws IOException {
        List<SpamEmailResponseDTO> responseDTO = gmailService.getSpamGmail(authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/mails/search/userEmail")
    public ResponseEntity<List<ImportantEmailResponseDTO>> searchGmailsByUserEmail(@Valid @RequestBody SearchGmailsByUserEmailRequestDTO requestDTO, Errors errors, Authentication authentication) throws IOException {
        if(errors.hasErrors()) {
            throw new NotValidArgumentException("not valid argument", errors);
        }

        List<ImportantEmailResponseDTO> responseDTO = gmailService.searchGmailsByUserEmail(requestDTO, authentication);
        return ResponseEntity.ok(responseDTO);
    }
}
