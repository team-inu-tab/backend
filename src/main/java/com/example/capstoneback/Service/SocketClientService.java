package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class SocketClientService {

    @Value("${AI_IP_ADDRESS}")
    private String AI_IP_ADDRESS;

    private final UserRepository userRepository;

    public String sendMessage(String message, Authentication authentication) {
        String serverIp = AI_IP_ADDRESS;
        int serverPort = 8080;

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String department = user.getStudentDepartment();
        Integer studentNum = user.getStudentNum();
        String studentName = user.getStudentName();
        String phoneNumber = user.getPhoneNumber();

        try (Socket socket = new Socket(serverIp, serverPort);
             OutputStream output = socket.getOutputStream();
             InputStream input = socket.getInputStream()) {

            output.write(message.getBytes());
            output.flush();

            byte[] buffer = new byte[2048];
            int bytesRead = input.read(buffer);
            String response = new String(buffer, 0, bytesRead);

            // {"message":" 로 시작하면 제거
            if (response.startsWith("{\"message\":\"")) {
                response = response.substring(12);
                response = response.replace("\\n", "\n").replace("\\\"", "\"");
            }

            // 치환 처리
            boolean hasDept = false, hasNum = false, hasName = false, hasPhone = false;

            if (response.contains("<학과>") || response.contains("학과")) {
                response = response.replace("<학과>", department).replace("학과", department);
                hasDept = true;
            }
            if (response.contains("<학번>") || response.contains("학번")) {
                response = response.replace("<학번>", studentNum.toString()).replace("학번", studentNum.toString());
                hasNum = true;
            }
            if (response.contains("<이름>") || response.contains("이름")) {
                response = response.replace("<이름>", studentName).replace("이름", studentName);
                hasName = true;
            }
            if (response.contains("<연락처>") || response.contains("<<연락처>>") || response.contains("연락처")) {
                response = response
                        .replace("<연락처>", phoneNumber)
                        .replace("<<연락처>>", phoneNumber)
                        .replace("연락처", phoneNumber);
                hasPhone = true;
            }

            StringBuilder appendInfo = new StringBuilder();
            if (!hasDept) appendInfo.append("\n학과: ").append(department);
            if (!hasNum) appendInfo.append("\n학번: ").append(studentNum);
            if (!hasName) appendInfo.append("\n이름: ").append(studentName);
            if (!hasPhone) appendInfo.append("\n연락처: ").append(phoneNumber);

            return response + appendInfo;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}