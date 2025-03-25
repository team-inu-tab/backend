package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class SelfEmailResponseDTO {
    private String id;
    private String title;
    private String content;
    private LocalDateTime sendAt;
    private Boolean isStarred;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public SelfEmailResponseDTO(String id, String title, String content, LocalDateTime sendAt, Boolean isStarred, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sendAt = sendAt;
        this.isStarred = isStarred;
        this.fileNameList = fileNameList;
    }
}
