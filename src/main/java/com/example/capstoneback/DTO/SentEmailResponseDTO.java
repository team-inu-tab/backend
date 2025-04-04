package com.example.capstoneback.DTO;

import com.google.api.services.gmail.model.MessagePart;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class SentEmailResponseDTO {
    private String id;
    private String title;
    private MessagePart content;
    private String receiver;
    private LocalDateTime sendAt;
    private Boolean isImportant;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public SentEmailResponseDTO(String id, String title, MessagePart content, String receiver, LocalDateTime sendAt, Boolean isImportant, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.isImportant = isImportant;
        this.fileNameList = fileNameList;
    }
}
