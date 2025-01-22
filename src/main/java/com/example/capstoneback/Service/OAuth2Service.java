package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.ReissueRequestDTO;
import com.example.capstoneback.DTO.ReissueResponseDTO;
import com.example.capstoneback.Entity.Token;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Jwt.JwtUtil;
import com.example.capstoneback.Repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public ReissueResponseDTO reissue(ReissueRequestDTO requestDTO){

        String refreshToken = requestDTO.getRefreshToken();

        //토큰이 유무 확인
        if(refreshToken == null){
            return ReissueResponseDTO.builder().accessToken(null).build();
        }

        //토큰 만료 확인
        try{
            jwtUtil.isExpired(refreshToken);
        }catch (ExpiredJwtException e){
            System.out.println("refresh token is expired");
            return ReissueResponseDTO.builder().accessToken(null).build();
        }

        //토큰이 만료 및 올바른 형식의 토큰인지 확인
        try{
            jwtUtil.isExpired(refreshToken);
        }catch (ExpiredJwtException e){
            System.out.println("token expired");
            return ReissueResponseDTO.builder().accessToken(null).build();
        }catch(MalformedJwtException | IllegalArgumentException e){
            System.out.println("invalid JWT");
            return ReissueResponseDTO.builder().accessToken(null).build();
        }

        //토큰의 category가 refresh-token인지 확인
        if(!jwtUtil.getCategory(refreshToken).equals("refresh-token")){
            System.out.println("invalid token");
            return ReissueResponseDTO.builder().accessToken(null).build();
        }

        //db에서 리프레시 토큰 확인
        Token existRefreshToken = tokenRepository.findByRefreshToken(refreshToken);
        if(existRefreshToken == null){
            return ReissueResponseDTO.builder().accessToken(null).build();
        }
        User user = existRefreshToken.getUser();

        //기존 리프레시 토큰 삭제
        tokenRepository.delete(existRefreshToken);

        //액세스 토큰, 리프레시 토큰 생성
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access-token", username, role, 10*60*1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh-token", username, role, 24*60*60*1000L);

        //리프레시 토큰 db 저장
        Token refreshTokenEntity = Token.builder()
                .refreshToken(newRefreshToken)
                .expirationAt(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();

        tokenRepository.save(refreshTokenEntity);

        return ReissueResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
