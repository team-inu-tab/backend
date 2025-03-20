package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.EmailRequestDTO;
import com.example.capstoneback.DTO.SendMailRequestDTO;
import com.example.capstoneback.Service.AmazonMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class SendMailController {
    private final AmazonMailService amazonMailService;

    // 메일 송신
    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody SendMailRequestDTO request) {
        amazonMailService.sendMail(request);
        return ResponseEntity.ok("메일 전송 완료");
    }

    // 메일 수신 및 db에 저장
    @PostMapping("/receive")
    public ResponseEntity<String> receiveMail(@RequestBody EmailRequestDTO emailRequestDTO) {
        System.out.println("test1");
        amazonMailService.receiveMail(emailRequestDTO);
        return ResponseEntity.ok("메일 수신 및 저장 완료");
    }
}
