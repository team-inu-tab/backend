package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class ReceivedEmailResponseDTO {
    private String id;
    private String title;
    private String content;
    private String sender;
    private LocalDateTime receiveAt;
    private Boolean isStarred;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public ReceivedEmailResponseDTO(String id, String title, String content, String sender, LocalDateTime receiveAt, Boolean isStarred, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiveAt = receiveAt;
        this.isStarred = isStarred;
        this.fileNameList = fileNameList;
    }
}
