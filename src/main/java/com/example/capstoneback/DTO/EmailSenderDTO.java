package com.example.capstoneback.DTO;

import lombok.Getter;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.model.*;

@Getter
@Component
public class EmailSenderDTO {

    public SendEmailRequest toSendRequestDTO(SendMailRequestDTO request) {
        Destination destination = Destination.builder()
                .toAddresses(request.getTo()) // 수신자 이메일
                .build();

        Message message = Message.builder()
                .subject(createContent(String.format("[test] - %s", request.getSubject())))
                .body(Body.builder()
                        .html(createContent(createHtmlBody(request))) // HTML 본문 추가
                        .build())
                .build();

        return SendEmailRequest.builder()
                .source(request.getFrom()) // 발신자 이메일
                .destination(destination) // 수신자 이메일 설정
                .message(message) // 이메일 내용 설정
                .build();
    }

    private Content createContent(String text) {
        return Content.builder()
                .charset("UTF-8")
                .data(text)
                .build();
    }

    private String createHtmlBody(SendMailRequestDTO request) {
        return "<div style=\"font-family: Arial, sans-serif;\">" +
                "<div style=\"margin-bottom: 20px;\">" +
                "<strong>작성자 이메일:</strong> " + request.getFrom() + "</div>" +
                "<div style=\"margin-bottom: 20px;\">" +
                "<strong>제목:</strong> " + request.getSubject() + "</div>" +
                "<div style=\"margin-bottom: 20px;\">" +
                "<strong>내용:</strong> " + request.getContent() + "</div>" +
                "</div>";
    }
}