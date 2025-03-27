package com.example.capstoneback.Controller;

import com.example.capstoneback.Service.GmailService;
import com.example.capstoneback.Service.MultiFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MultiFileController {

    private final MultiFileService multiFileService;
    private final GmailService gmailService;

    // 테스트용 파일 등록 API
    @PostMapping("/fileUploadSample")
    public ResponseEntity<?> fileSample(@RequestParam("file") MultipartFile file) throws IOException {
        multiFileService.saveFile(file, 1L);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mails/{id}/file/{attachmentId}")
    public ResponseEntity<String> findMultiFile(@PathVariable("id") String mailiId, @PathVariable("attachmentId") String attachmentId, Authentication authentication) throws IOException, IllegalAccessException {
        String fileResource = gmailService.getFileResource(mailiId, attachmentId, authentication);
        return ResponseEntity.ok().body(fileResource);
    }
}
