package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.ReceivedEmailResponseDTO;
import com.example.capstoneback.Service.SpamEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpamEmailController {

    private final SpamEmailService spamEmailService;

    // 스팸 메일함으로 이동
    @PostMapping("/mails/spam/{messageId}")
    public ResponseEntity<String> addToSpam(@PathVariable String messageId, Authentication authentication) {
        try {
            spamEmailService.addToSpam(messageId, authentication);
            return ResponseEntity.ok("스팸메일함 이동 성공");
        } catch (IOException e) {
            System.out.println(e);
            return ResponseEntity
                    .internalServerError()
                    .body("스팸메일 이동 실패: " + e.getMessage());
        }
    }

    // 스팸 해제 (받은 편지함으로 복원)
    @DeleteMapping("/mails/spam/{messageId}")
    public ResponseEntity<String> removeFromSpam(@PathVariable String messageId, Authentication authentication) {
        try {
            spamEmailService.removeFromSpam(messageId, authentication);
            return ResponseEntity.ok("메일이 받은편지함으로 복원되었습니다.");
        } catch (IOException e) {
            System.out.println(e);
            return ResponseEntity
                    .internalServerError()
                    .body("스팸메일 해제 실패: " + e.getMessage());
        }
    }
}
