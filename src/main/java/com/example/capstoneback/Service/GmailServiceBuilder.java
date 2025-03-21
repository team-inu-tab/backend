package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailServiceBuilder {
    private static final String APPLICATION_NAME = "My Gmail Integration App";
    final static JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    final static HttpTransport httpTransport = new NetHttpTransport();

    private final GoogleCredentialService googleCredentialService;

    public Gmail getGmailService(User user) throws Exception {

        Credential credential = googleCredentialService.getCredentialFromRefreshToken(user.getUsername());


        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("My Gmail App")
                .build();
    }
}
