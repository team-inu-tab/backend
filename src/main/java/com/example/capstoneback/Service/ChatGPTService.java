package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.GptRequest;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final UserRepository userRepository;

    @Value("${CHATGPT_API_KEY}")
    private String API_KEY;

    @Value("${CHATGPT_API_URL}")
    private String API_URL;

    public String generateChatGPTResponse(GptRequest requestBody, Authentication authentication) {
        String userMessage = requestBody.getMessage();

        // 1) 유저 정보 조회
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST));

        String department = user.getStudentDepartment();
        Integer studentNum = user.getStudentNum();
        String studentName = user.getStudentName();
        // String phoneNumber = user.getPhoneNumber(); // DB 필드가 있다면

        // 2) 템플릿 치환
        String developerMessageTemplate = """
            A university student is writing an email to a professor.
            I want to make sure the tone of the email is polite and formal.
            Be sure to include name, student number, and phone number.
            Use the following example to edit the mail.
            input: 안녕하세요 제 데이터베이스 강의 중간고사 성적 알고 싶어요.
            output: <교수님 성함> 교수님께 \\n\\n안녕하십니까 교수님\\n저는 이번 학기 데이터베이스 강의 수강 중인 <학과> <학번> <이름> 입니다.\\n\\n이번 데이터베이스 중간고사 성적을 확인하고 싶어 메일드립니다.\\n가능하신 시간대를 알려주시면 찾아뵙도록 하겠습니다.\\n\\n감사합니다.\\n<이름> 드림\\n<전화번호>
            input: 연구실 방문 예약하고 싶습니다.
            output: <교수님 성함> 교수님께 \\n\\n안녕하십니까 교수님\\n저는 <학과> <학번> <이름> 입니다.\\n\\n<연구실 이름> 연구실에 관심 있어 면담을 요청드리고자 연락드렸습니다.\\n가능하신 시간대를 알려주시면 찾아뵙도록 하겠습니다.\\n\\n감사합니다.\\n<이름> 드림\\n<전화번호>
        """;

        // null 방지 (필요하다면)
        String deptStr = department != null ? department : "";
        String studNumStr = studentNum != null ? studentNum.toString() : "";
        String studNameStr = studentName != null ? studentName : "";
        // String phoneNumberStr = phoneNumber != null ? phoneNumber : "";

        String developerMessage = developerMessageTemplate
                .replace("<학과>", deptStr)
                .replace("<학번>", studNumStr)
                .replace("<이름>", studNameStr);
        // .replace("<전화번호>", phoneNumberStr);

        // 3) ChatGPT API 요청 준비
        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o-mini");
        json.put("max_tokens", 300);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", userMessage));
        messages.put(new JSONObject().put("role", "system").put("content", developerMessage));
        json.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);

        // 4) API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                API_URL,
                requestEntity,
                String.class
        );

        // 5) 응답 성공/실패 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            JSONObject responseJson = new JSONObject(responseBody);

            String result = responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return result;
        } else {
            // 실패 시 원하는 형식으로 throw
            throw new RuntimeException("API 오류: " + response.getBody());
        }
    }
}
