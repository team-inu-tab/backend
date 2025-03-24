package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DraftEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String receiver;
    private LocalDateTime createdAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public DraftEmailResponseDTO(Long id, String title, String content, String receiver, LocalDateTime createdAt, Boolean isImportant, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
