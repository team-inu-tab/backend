package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.ReceivedEmailResponseDTO;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Repository.UserRepository;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SpamEmailService {

    private final UserRepository userRepository;

    //Http 요청 객체와 Json 변환 객체 생성
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    // 스팸 추가
    public void addToSpam(String messageId, Authentication authentication) throws IOException {
        User user = getUserFromAuth(authentication);
        Gmail gmail = getGmailService(user);

        // gmail api에서 메일에 라벨을 추가 or 제거
        ModifyMessageRequest request = new ModifyMessageRequest()
                .setAddLabelIds(List.of("SPAM"))
                .setRemoveLabelIds(List.of("INBOX"));

        gmail.users().messages().modify(user.getEmail(), messageId, request).execute();
    }

    // 스팸 해제
    public void removeFromSpam(String messageId, Authentication authentication) throws IOException {
        User user = getUserFromAuth(authentication);
        Gmail gmail = getGmailService(user);

        // gmail api에서 메일에 라벨을 추가 or 제거
        ModifyMessageRequest request = new ModifyMessageRequest()
                .setAddLabelIds(List.of("INBOX"))
                .setRemoveLabelIds(List.of("SPAM"));

        gmail.users().messages().modify(user.getEmail(), messageId, request).execute();
    }

    // 스팸 목록 조회


    // 유저 조회
    private User getUserFromAuth(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST));
    }

    // Gmail 서비스 생성
    private Gmail getGmailService(User user) {
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(user.getAccessToken(), null));
        return new Gmail.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("maeil-mail")
                .build();
    }
}
