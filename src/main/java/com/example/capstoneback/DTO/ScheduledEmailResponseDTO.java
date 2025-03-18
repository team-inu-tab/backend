package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduledEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String receiver;
    private LocalDateTime scheduledAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public ScheduledEmailResponseDTO(Long id, String title, String content, String receiver, LocalDateTime scheduledAt, boolean isImportant, boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.scheduledAt = scheduledAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
