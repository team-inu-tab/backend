package com.example.capstoneback.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MailRequestDTO {
    @Setter
    @Getter
    public static class EmailValue {
        private String value;
    }

    private List<EmailValue> toEmail;
    private String subject;
    private String body;
}
