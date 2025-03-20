package com.example.capstoneback.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailServiceBuilder {
    private static final String APPLICATION_NAME = "My Gmail Integration App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final GoogleCredentialService googleCredentialService;

    public Gmail getGmailService(String username) throws Exception {
        // 위에서 만든 getUserGoogleCredentials() 로 Credential 생성
        var credential = googleCredentialService.getUserGoogleCredentials(username);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
