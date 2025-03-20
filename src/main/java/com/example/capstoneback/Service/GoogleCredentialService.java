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

    public Credential getUserGoogleCredentials(String username) {
        // 1) User 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));

        // 2) 가장 최근에 생성된(id가 가장 큰)
        Token token = tokenRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new RuntimeException("해당 사용자의 토큰이 존재하지 않습니다."));

        // 3) RefreshToken 확인
        String refreshToken = token.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("리프레시 토큰이 비어 있습니다.");
        }

        // 4) Google Credential 생성
        CredentialRefreshListener refreshListener = new CredentialRefreshListener() {
            @Override
            public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
                // Access Token이 갱신되면서 새로운 Refresh Token이 온 경우
                String newRefreshToken = credential.getRefreshToken();
                if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                    token.setRefreshToken(newRefreshToken);
                    tokenRepository.save(token);
                }
            }

            @Override
            public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
                System.err.println("토큰 재발급 오류: " + tokenErrorResponse.getErrorDescription());
            }
        };

        return new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .addRefreshListener(refreshListener)
                .build()
                .setRefreshToken(refreshToken);
    }
}
