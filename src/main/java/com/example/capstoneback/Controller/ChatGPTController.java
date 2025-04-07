package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.GptRequest;
import com.example.capstoneback.Service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    @PostMapping("/api/gpt")
    public ResponseEntity<String> generateResponse(
            @RequestBody GptRequest requestBody,
            Authentication authentication
    ) {
        try {
            // 성공시 결과 반환
            String result = chatGPTService.generateChatGPTResponse(requestBody, authentication);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("요청 실패", e);
            return ResponseEntity
                    .internalServerError()
                    .body("요청 실패: " + e.getMessage());
        }
    }
}
