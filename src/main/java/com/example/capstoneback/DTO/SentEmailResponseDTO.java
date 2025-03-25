package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SentEmailResponseDTO {
    private String id;
    private String title;
    private String content;
    private String receiver;
    private LocalDateTime sendAt;
    private Boolean isStarred;
    private Boolean isFileExist;

    @Builder
    public SentEmailResponseDTO(String id, String title, String content, String receiver, LocalDateTime sendAt, Boolean isStarred, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.isStarred = isStarred;
        this.isFileExist = isFileExist;
    }
}
