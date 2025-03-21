package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.Token;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.TokenRepository;
import com.example.capstoneback.Repository.UserRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GoogleCredentialService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private static final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_GOOGLE_CLIENT_SECRET";

    public Credential getCredentialFromRefreshToken(String username) {
        // 1. 사용자 정보 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // 2. 저장된 최신 토큰 가져오기
        Token token = tokenRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new RuntimeException("해당 사용자의 토큰이 존재하지 않습니다."));

        String refreshToken = token.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("refresh token이 비어 있습니다.");
        }

        // 3. CredentialRefreshListener 정의
        CredentialRefreshListener refreshListener = new CredentialRefreshListener() {
            @Override
            public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
                // 새로운 RefreshToken이 오면 DB에 갱신
                if (tokenResponse.getRefreshToken() != null) {
                    token.setRefreshToken(tokenResponse.getRefreshToken());
                    tokenRepository.save(token);
                }
            }

            @Override
            public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
                String error = tokenErrorResponse == null ? "Unknown error" : tokenErrorResponse.getErrorDescription();
                System.err.println("⚠️ Google Token Error: " + error);
            }
        };

        // 4. GoogleCredential 생성 및 리턴
        return new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .addRefreshListener(refreshListener)
                .build()
                .setRefreshToken(refreshToken);
    }
}

