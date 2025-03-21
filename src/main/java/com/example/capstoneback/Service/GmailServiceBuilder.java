package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailServiceBuilder {
    private static final String APPLICATION_NAME = "My Gmail Integration App";
    final static JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    final static HttpTransport httpTransport = new NetHttpTransport();

    private final GoogleCredentialService googleCredentialService;

    public Gmail getGmailService(OAuth2AccessToken oAuth2AccessToken) throws Exception {

        GoogleCredentials credentials = GoogleCredentials.create(
                new AccessToken(oAuth2AccessToken.getTokenValue(),
                        null));

        return new Gmail.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .setApplicationName("My Gmail App")
                .build();
    }
}
