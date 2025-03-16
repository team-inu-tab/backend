package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.ReceivedEmailResponseDTO;
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
}
