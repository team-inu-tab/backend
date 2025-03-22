package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MailDetailsResponseDTO {
    private String title;
    private String content;
    private String sender;
    private String receiver;
    private LocalDateTime sendAt;
    private LocalDateTime receivedAt;
    private Boolean isImportant;
    private List<String> fileName;

    @Builder
    public MailDetailsResponseDTO(String title, String content, String sender, String receiver, LocalDateTime sendAt, LocalDateTime receivedAt, boolean isImportant, List<String> fileName) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.receivedAt = receivedAt;
        this.isImportant = isImportant;
        this.fileName = fileName;
    }
}
