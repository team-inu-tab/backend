package com.example.capstoneback.DTO;

import com.google.api.services.gmail.model.MessagePart;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Getter
public class ImportantEmailResponseDTO {
    private String id;
    private String mailType;
    private String title;
    private MessagePart content;
    private String sender;
    private String receiver;
    private LocalDateTime receiveAt;
    private LocalDateTime sendAt;
    private List<HashMap<String, String>> fileNameList;

    @Builder
    public ImportantEmailResponseDTO(String id, String title, String mailType, MessagePart content, String sender, String receiver, LocalDateTime receiveAt, LocalDateTime sendAt, List<HashMap<String, String>> fileNameList) {
        this.id = id;
        this.mailType = mailType;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.receiveAt = receiveAt;
        this.sendAt = sendAt;
        this.fileNameList = fileNameList;
    }
}
