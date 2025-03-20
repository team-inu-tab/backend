package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.SendMailRequestDTO;
import com.example.capstoneback.Service.AmazonMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class SendMailController {
    private final AmazonMailService amazonMailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody SendMailRequestDTO request) {
        amazonMailService.sendMail(request);
        return ResponseEntity.ok("메일 전송 완료");
    }
}
