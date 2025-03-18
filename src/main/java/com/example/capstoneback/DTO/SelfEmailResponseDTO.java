package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SelfEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime sendAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public SelfEmailResponseDTO(Long id, String title, String content, LocalDateTime sendAt, Boolean isImportant, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sendAt = sendAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
