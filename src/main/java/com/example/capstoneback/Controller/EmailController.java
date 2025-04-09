package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.*;
import com.example.capstoneback.Service.GmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

//    private final EmailService emailService;
    private final GmailService gmailService;

    @GetMapping("/mails/receive")
    public ResponseEntity<Map<String, Object>> getReceivedEmails(@RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getReceivedGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/send")
    public ResponseEntity<Map<String, Object>> getSentEmails(@RequestParam(name = "pageToken", required = false) String pageToken,Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getSentGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("mails/send/{mailId}")
    public ResponseEntity<SentEmailResponseDTO> getSentMailById(@PathVariable String mailId, Authentication authentication) throws IOException {
        return ResponseEntity.ok(gmailService.getSentGmailWithMailId(mailId, authentication));
    }

    @GetMapping("/mails/self")
    public ResponseEntity<Map<String, Object>> getSelfEmail(@RequestParam(name = "pageToken", required = false) String pageToken,Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getSelfGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/mails/important")
    public ResponseEntity<Map<String, Object>> getImportantEmails(@RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getImportantGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

//    @GetMapping("/mails/schedule")
//    public ResponseEntity<List<ScheduledEmailResponseDTO>> getScheduledEmails(Authentication authentication){
//        List<ScheduledEmailResponseDTO> responseDTO = emailService.getScheduledEmails(authentication);
//        return ResponseEntity.ok(responseDTO);
//    }

    @GetMapping("/mails/draft")
    public ResponseEntity<Map<String, Object>> getDraftEmails(@RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getDraftGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

//    @GetMapping("/mails/{id}")
//    public ResponseEntity<MailDetailsResponseDTO> getEmailDetails(@PathVariable("id") Long mailId, Authentication authentication) throws IllegalAccessException {
//        MailDetailsResponseDTO responseDTO = emailService.getMailDetails(mailId ,authentication);
//        return ResponseEntity.ok(responseDTO);
//    }

    @GetMapping("/mails/spam")
    public ResponseEntity<Map<String, Object>> getSpamEmails(@RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getSpamGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/mails/search/userEmail")
    public ResponseEntity<Map<String, Object>> searchGmailsByUserEmail(@RequestBody SearchGmailsByUserEmailRequestDTO requestDTO, @RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.searchGmailsByUserEmail(requestDTO, pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/mails/trash/temporary")
    public ResponseEntity<Map<String, Object>> deleteGmailTemporary(@RequestBody DeleteGmailTemporaryRequestDTO requestDTO, Authentication authentication) throws IOException {
        List<String> movedToTrashMailIds = gmailService.deleteGmailTemporary(requestDTO, authentication);
        Map<String, Object> response = new HashMap<>();
        response.put("movedToTrashMailIds", movedToTrashMailIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/mails/trash/permanent")
    public ResponseEntity<Map<String, Object>> deleteGmailPermanent(@RequestBody DeleteGmailPermanentRequestDTO requestDTO, Authentication authentication){
        List<String> deletedMailIds = gmailService.deleteGmailPermanent(requestDTO, authentication);
        Map<String, Object> response = new HashMap<>();
        response.put("deletedMailIds", deletedMailIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mails/trash")
    public ResponseEntity<Map<String, Object>> getTrashEmails(@RequestParam(name = "pageToken", required = false) String pageToken, Authentication authentication) throws IOException {
        Map<String, Object> responseDTO = gmailService.getTrashGmail(pageToken, authentication);
        return ResponseEntity.ok(responseDTO);
    }
}
