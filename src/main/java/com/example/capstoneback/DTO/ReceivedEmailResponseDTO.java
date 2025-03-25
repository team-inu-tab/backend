package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReceivedEmailResponseDTO {
    private String id;
    private String title;
    private String content;
    private String sender;
    private LocalDateTime receiveAt;
    private Boolean isStarred;
    private Boolean isFileExist;

    @Builder
    public ReceivedEmailResponseDTO(String id, String title, String content, String sender, LocalDateTime receiveAt, Boolean isStarred, Boolean isFileExist) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiveAt = receiveAt;
        this.isStarred = isStarred;
        this.isFileExist = isFileExist;
    }
}
