package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SentEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String receiver;
    private LocalDateTime sendAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public SentEmailResponseDTO(Long id, String title, String content, String receiver, LocalDateTime sendAt, Boolean isImportant, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
