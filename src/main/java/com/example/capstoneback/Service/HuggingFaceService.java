package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.HuggingFaceRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    // 허깅페이스 AccessToken 값
    @Value("${HUGGINGFACE_API_KEY}")
    private String API_KEY;

    //허깅페이스 모델 API url
    @Value("${HUGGINGFACE_API_URL}")
    private String API_URL;

    public String getDataFromHuggingFace(HuggingFaceRequestDTO requestDTO){

        // http 요청 템플릿 생성
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정(모델 사용에 필요한 값에 따라 달라짐)
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", requestDTO.getMessage());

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("messages", messages);
        requestData.put("max_tokens", 500);
        requestData.put("model", "deepseek-ai/DeepSeek-V3-0324-fast");
        requestData.put("stream", false);

        // http 요청 객체 생성
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        // 템플릿에 값 입력 후 요청 실행
        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        return response.getBody();
    }
}
