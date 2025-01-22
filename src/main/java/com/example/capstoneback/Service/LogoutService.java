package com.example.capstoneback.Service;

import com.example.capstoneback.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenRepository tokenRepository;

    @Transactional
    public boolean checkAndDeleteRefreshToken(String refresh) {
        // DB에 저장되어 있는지 확인
        boolean isExist = tokenRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            return false;
        }

        // Refresh 토큰 DB에서 제거
        tokenRepository.deleteByRefreshToken(refresh);
        return true;
    }
}