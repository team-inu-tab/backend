package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ImportantEmailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String sender;
    private String receiver;
    private LocalDateTime receiveAt;
    private LocalDateTime sendAt;
    private Boolean isImportant;
    private Boolean isFileExist;

    @Builder
    public ImportantEmailResponseDTO(Long id, String title, String content, String sender, String receiver, LocalDateTime receiveAt, LocalDateTime sendAt, boolean isImportant, boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.receiveAt = receiveAt;
        this.sendAt = sendAt;
        this.isImportant = isImportant;
        this.isFileExist = isFileExist;
    }
}
