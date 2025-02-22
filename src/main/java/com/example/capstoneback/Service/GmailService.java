package com.example.capstoneback.Service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GmailService {

    public List<Message> getEmails(OAuth2AccessToken oAuth2AccessToken) throws IOException {

        //Http 요청 객체와 Json 변환 객체 생성
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // OAuth2 AccessToken을 GoogleCredentials로 변환
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(oAuth2AccessToken.getTokenValue(), null));

        //Gmail api 요청 객체 생성
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, null)
                .setApplicationName("Spring Gmail API Example")
                .setHttpRequestInitializer(new HttpCredentialsAdapter(credentials))
                .build();

        // 이메일 요청
        ListMessagesResponse messagesResponse = gmail.users().messages().list("me").setMaxResults(10L).execute();

        //이메일 반환
        return messagesResponse.getMessages();
    }

}
