package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.GmailSendFailedException;
import com.example.capstoneback.Repository.UserRepository;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
     * 1) MimeMessage를 만드는 헬퍼 함수 그런데 이제 첨부파일이 들어간
     */
    private MimeMessage createEmailWithAttachment(String from,
                                                  String to,
                                                  String subject,
                                                  String bodyText,
                                                  File attachment) throws Exception {
        Properties props = new Properties();
        Session session = Session.getInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, StandardCharsets.UTF_8.name());

        // 본문 생성
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(bodyText, StandardCharsets.UTF_8.name());

        // 첨부파일 Part 생성
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment); // File에서 읽음

        // 둘을 묶기
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart);
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
        message = service.users().messages().send("me", message).execute();

        System.out.println("Message id: " + message.getId());
        // 전체 JSON을 출력
        System.out.println(message.toPrettyString());
        return message;
    }

    /**
     * 외부에서 호출되는 실제 메일 보내기
     */
//    public void sendEmail(Authentication authentication, String toEmail, String subject, String body) {
//        try {
//            String username = authentication.getName();
//            User user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
//
//            // 서버 저장된 Refresh Token으로 Gmail API 인증
//            Gmail service = gmailServiceBuilder.getGmailService(user);
//
//            MimeMessage emailContent = createEmail(
//                    user.getEmail(), toEmail, subject, body
//            );
//
//            Message message = sendMessage(service, user.getUsername(), emailContent);
//            System.out.println("이메일 전송 성공. ID: " + message.getId());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GmailSendFailedException(ErrorCode.USER_DOESNT_EXIST);
//        }
//    }
    /**
     * 외부에서 호출되는 실제 메일 보내기 그런데 이제 첨부파일이 들어갈수도 있고 아닐수도 있고
     */
    public void sendEmail(Authentication authentication, String toEmail, String subject, String body, MultipartFile attachmentFile) {
        File tempFile = null;

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
            // 서버 저장된 Refresh Token으로 Gmail API 인증
            Gmail service = gmailServiceBuilder.getGmailService(user);

            MimeMessage emailContent;

            // 전송파일이 있을경우
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                tempFile = File.createTempFile("upload-", attachmentFile.getOriginalFilename());
                attachmentFile.transferTo(tempFile);

                emailContent = createEmailWithAttachment(
                        user.getEmail(), toEmail, subject, body, tempFile
                );
            } else {
                // 전송파일이 없는 경우
                emailContent = createEmail(user.getEmail(), toEmail, subject, body);
            }

            // 기존 이메일 전송 로직
            Message message = sendMessage(service, "me", emailContent);
            System.out.println("이메일 전송 성공. ID: " + message.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new GmailSendFailedException(ErrorCode.USER_DOESNT_EXIST);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // 메일 전송 후 임시파일 삭제
            }
        }
    }

    // 임시메일 저장
    public void saveEmailToDraft(Authentication authentication, String toEmail, String subject, String body) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

            Gmail service = gmailServiceBuilder.getGmailService(user);
            MimeMessage emailContent = createEmail(user.getEmail(), toEmail, subject, body);

            // MimeMessage → Message 변환
            Message message = createMessageWithEmail(emailContent);

            // draft에 저장
            com.google.api.services.gmail.model.Draft draft = new com.google.api.services.gmail.model.Draft();
            draft.setMessage(message);

            draft = service.users().drafts().create("me", draft).execute();

            System.out.println("Draft saved successfully. Draft ID: " + draft.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new GmailSendFailedException(ErrorCode.USER_DOESNT_EXIST);
        }
    }

    // 임시메일 수정
    public void updateDraftEmail(Authentication authentication, String draftId, String toEmail, String subject, String body) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

            Gmail service = gmailServiceBuilder.getGmailService(user);

            MimeMessage emailContent = createEmail(user.getEmail(), toEmail, subject, body);
            Message message = createMessageWithEmail(emailContent);

            Draft draft = new Draft();
            draft.setMessage(message);
            draft.setId(draftId);

            service.users().drafts().update("me", draftId, draft).execute();

            System.out.println("Draft 업데이트 성공 (Draft ID: " + draftId + ")");

        } catch (Exception e) {
            e.printStackTrace();
            throw new GmailSendFailedException(ErrorCode.USER_DOESNT_EXIST);
        }
    }

    // 임시메일 삭제
    public void deleteDraftEmail(Authentication authentication, String draftId) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

            Gmail service = gmailServiceBuilder.getGmailService(user);

            service.users().drafts().delete("me", draftId).execute();

            System.out.println("Draft 삭제 성공 (Draft ID: " + draftId + ")");

        } catch (Exception e) {
            e.printStackTrace();
            throw new GmailSendFailedException(ErrorCode.USER_DOESNT_EXIST);
        }
    }

}
