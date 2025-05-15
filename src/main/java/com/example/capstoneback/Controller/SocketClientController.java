package com.example.capstoneback.Controller;

import com.example.capstoneback.Service.SocketClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocketClientController {

    private final SocketClientService socketClientService;

    @PostMapping("/api/gpt")
    public ResponseEntity<String> sendMessage(@RequestBody String message, Authentication authentication) {
        try {
            String result = socketClientService.sendMessage(message, authentication);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body(result);
        } catch (Exception e) {
            log.error("요청 실패", e);
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body("요청 실패: " + e.getMessage());
        }
    }
}