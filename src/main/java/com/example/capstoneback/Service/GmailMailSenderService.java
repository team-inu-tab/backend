package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.UserRepository;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
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

    /**
     * 1) MimeMessage를 만드는 헬퍼 함수
     */
    private MimeMessage createEmail(String from,
                                    String to,
                                    String subject,
                                    String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getInstance(props, null);

        // MimeMessage 생성
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, StandardCharsets.UTF_8.name());
        email.setText(bodyText, StandardCharsets.UTF_8.name());
        return email;
    }

    /**
     * 2) 공식문서 예시: createMessageWithEmail
     *    MimeMessage → ByteArrayOutputStream → Base64 URL-safe → setRaw
     */
    private Message createMessageWithEmail(MimeMessage emailContent) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] rawEmailBytes = buffer.toByteArray();

        // Base64 URL-safe 인코딩
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawEmailBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * 3) 실제 전송 (sendMessage)
     */
    private Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws Exception {
        // MimeMessage → com.google.api.services.gmail.model.Message 로 변환
        Message message = createMessageWithEmail(emailContent);
        // Gmail API 송신
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        // 전체 JSON을 출력
        System.out.println(message.toPrettyString());
        return message;
    }

    /**
     * 외부에서 호출되는 실제 메일 보내기
     */
    public void sendEmail(Authentication authentication, String toEmail, String subject, String body) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

            // 서버 저장된 Refresh Token으로 Gmail API 인증
            System.out.println("2번2번2번2번2번2번2번2번2번2번2번2번2번2번");
            Gmail service = gmailServiceBuilder.getGmailService(user);

            MimeMessage emailContent = createEmail(
                    user.getEmail(), toEmail, subject, body
            );

            Message message = sendMessage(service, user.getUsername(), emailContent);
            System.out.println("✅ 이메일 전송 성공. ID: " + message.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 이메일 전송 실패: " + e.getMessage(), e);
        }
    }
}
