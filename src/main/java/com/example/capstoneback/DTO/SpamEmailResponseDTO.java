package com.example.capstoneback.DTO;

import com.google.api.services.gmail.model.MessagePart;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class SpamEmailResponseDTO {
    private String id;
    private String title;
    private MessagePart content;
    private String sender;
    private LocalDateTime receiveAt;
    private Boolean isImportant;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public SpamEmailResponseDTO(String id, String title, MessagePart content, String sender, LocalDateTime receiveAt, Boolean isImportant, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiveAt = receiveAt;
        this.isImportant = isImportant;
        this.fileNameList = fileNameList;
    }
}
