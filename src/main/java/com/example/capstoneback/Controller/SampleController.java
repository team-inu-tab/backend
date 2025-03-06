package com.example.capstoneback.Controller;

import com.example.capstoneback.Service.MultiFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SampleController {

    private final MultiFileService multiFileService;

    private final Path rootLocation = Paths.get("upload_dir");

    // cd test 2
    @GetMapping("/")
    public String sampleMapping() {
        return "sample";
    }

    @GetMapping("/hello")
    @ResponseBody
    public ResponseEntity<?> hello() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Hello World");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/fileUploadSample")
    public ResponseEntity<?> fileSample(@RequestParam("file") MultipartFile file) throws IOException {
        multiFileService.saveFile(file);
        return ResponseEntity.ok().build();
    }
}
