package com.example.capstoneback.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {
    @GetMapping("/")
    public String sampleMapping(){
        return "sample";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello!";
    }
}
