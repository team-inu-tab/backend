package com.example.capstoneback.Controller;

import com.example.capstoneback.Service.MultiFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class MultiFileController {

    private final MultiFileService multiFileService;

    // 테스트용 파일 등록 API
    @PostMapping("/fileUploadSample")
    public ResponseEntity<?> fileSample(@RequestParam("file") MultipartFile file) throws IOException {
        multiFileService.saveFile(file, 1L);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mails/{id}/file/{fileName}")
    public ResponseEntity<Resource> findMultiFile(@PathVariable("id") Long mailiId, @PathVariable("fileName") String fileName, Authentication authentication) throws MalformedURLException, FileNotFoundException, IllegalAccessException, UnsupportedEncodingException {
        Resource resource = multiFileService.findFileByEmailIdAndFileName(mailiId, fileName, authentication);

        // 파일 이름 utf-8로 인코딩(한글일 경우 오류 발생)
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}
