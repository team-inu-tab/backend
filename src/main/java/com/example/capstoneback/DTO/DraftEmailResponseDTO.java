package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class DraftEmailResponseDTO {
    private String id;
    private String draftId;
    private String title;
    private String content;
    private String receiver;
    private LocalDateTime createdAt;
    private Boolean isImportant;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public DraftEmailResponseDTO(String id, String draftId, String title, String content, String receiver, LocalDateTime createdAt, Boolean isImportant, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.draftId = draftId;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.isImportant = isImportant;
        this.fileNameList = fileNameList;
    }
}
