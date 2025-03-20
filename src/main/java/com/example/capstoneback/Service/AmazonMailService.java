package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.EmailRequestDTO;
import com.example.capstoneback.DTO.SendMailRequestDTO;
import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.EmailRepository;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonMailService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    private final SesClient sesClient;

    // 이메일 송신
    public void sendMail(SendMailRequestDTO request) {
        try {
            // AWS SES에 맞는 이메일 요청 생성
            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(request.getTo()) // 수신자 메일 주소
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder().data(request.getSubject()).build()) // 제목
                            .body(Body.builder()
                                    .text(Content.builder().data(request.getContent()).build()) // 본문
                                    .build())
                            .build())
                    .source(request.getFrom()) // 발신자 이메일
                    .build();

            // 이메일 전송
            SendEmailResponse response = sesClient.sendEmail(sendEmailRequest);

            // 상태 확인
            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("이메일 전송 성공: {}", request.getTo());
            } else {
                log.error("이메일 전송 실패 Status Code: {}, Recipient: {}",
                        response.sdkHttpResponse().statusCode(), request.getTo());
            }
        } catch (SesException e) {
            log.error("SES Exception: {}", e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("이메일 전송 중 예상치 못한 에러: {}", e.getMessage(), e);
        }
    }

    // 이메일 수신

    @Transactional
    public void receiveMail(EmailRequestDTO emailDto) {

        LocalDateTime receivedAt;
        try {
            receivedAt = Instant.parse(emailDto.getTimestamp())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + emailDto.getTimestamp());
        }

        // 수신자가 시스템 유저인지 확인
        User user = userRepository.findByEmail(emailDto.getReceiver()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + emailDto.getReceiver());
        }

        // 이메일 저장
        Email email = Email.builder()
                .title(emailDto.getSubject())
                .content(emailDto.getContent())
                .sender(emailDto.getSender())
                .receiver(emailDto.getReceiver())
                .receiveAt(receivedAt) // 문자열을 LocalDateTime으로 변환
                .isImportant(false)
                .isDraft(false)
                .scheduledAt(null)
                .user(user)
                .build();

        emailRepository.save(email);
    }
}