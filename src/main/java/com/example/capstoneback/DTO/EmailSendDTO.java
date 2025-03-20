package com.example.capstoneback.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendDTO {
    private String toEmail;
    private String subject;
    private String body;
}
