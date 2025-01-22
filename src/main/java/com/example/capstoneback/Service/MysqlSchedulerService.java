package com.example.capstoneback.Service;

import com.example.capstoneback.Entity.Token;
import com.example.capstoneback.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MysqlSchedulerService {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void clearRefreshToken(){
        //만료기간이 지난 리프레시 토큰 삭제
        List<Token> tokenList = tokenRepository.findAll();
        for (Token token : tokenList) {
            if(token.getExpirationAt().isBefore(LocalDateTime.now())){
                tokenRepository.delete(token);
            }
        }
    }
}
