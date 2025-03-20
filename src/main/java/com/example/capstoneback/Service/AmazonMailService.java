package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.SendMailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonMailService {

    private final SesClient sesClient;
    private final Environment env;

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

            // 응답 상태 확인
            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("✅ Email sent successfully to: {}", request.getTo());
            } else {
                log.error("❌ Failed to send email. Status Code: {}, Recipient: {}",
                        response.sdkHttpResponse().statusCode(), request.getTo());
            }
        } catch (SesException e) {
            log.error("❌ SES Exception: {}", e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("❌ Unexpected Error while sending email: {}", e.getMessage(), e);
        }
    }
}