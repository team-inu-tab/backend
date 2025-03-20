package com.example.capstoneback.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message; // ← (Gmail API용)
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class GmailMailSenderService {

    private final GmailServiceBuilder gmailServiceBuilder;
    private final UserRepository userRepository;

    public void sendEmail(Authentication authentication, String toEmail, String subject, String body) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

            // 1) Gmail Service 생성
            Gmail service = gmailServiceBuilder.getGmailService(username);

            // 2) MIME 메시지 (jakarta.mail)
            Properties props = new Properties();
            Session session = Session.getInstance(props, null);

            // MimeMessage는 실제 메일 컨텐츠를 담음
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(user.getEmail()));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            email.setSubject(subject, StandardCharsets.UTF_8.name());
            email.setText(body, StandardCharsets.UTF_8.name());

            // MIME → 바이트 변환
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawEmailBytes = buffer.toByteArray();

            // Base64 URL-safe 인코딩
            String encodedEmail = Base64.getUrlEncoder().encodeToString(rawEmailBytes);

            // 3) Gmail API용 Message (com.google.api.services.gmail.model.Message)
            Message gmailMessage = new Message();
            gmailMessage.setRaw(encodedEmail);

            // 4) 전송
            Message response = service.users()
                    .messages()
                    .send("me", gmailMessage)
                    .execute();

            System.out.println("✅ 이메일 전송 성공: " + response.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 이메일 전송 실패: " + e.getMessage(), e);
        }
    }
}
