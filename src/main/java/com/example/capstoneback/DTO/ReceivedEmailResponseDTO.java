package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReceivedEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String sender;
    private LocalDateTime receiveAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public ReceivedEmailResponseDTO(Long id, String title, String content, String sender, LocalDateTime receiveAt, Boolean isImportant, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiveAt = receiveAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
