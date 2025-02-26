package com.example.capstoneback.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SampleController {
    // cd test 2
    @GetMapping("/")
    public String sampleMapping(){
        return "sample";
    }
    // 환경변수 테스트
    @GetMapping("/env/system")
    public Map<String, String> systemEnv() {
        return System.getenv();
    }

    @GetMapping("/hello")
    @ResponseBody
    public ResponseEntity<?> hello(){
        Map<String ,Object> map = new HashMap<>();
        map.put("message", "Hello World");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
