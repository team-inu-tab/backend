package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.ReissueRequestDTO;
import com.example.capstoneback.DTO.ReissueResponseDTO;
import com.example.capstoneback.Jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtUtil jwtUtil;

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

        //토큰이 정상적으로 존재할 경우 accessToken 발급해서 리턴
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access-token", username, role, 10*60*1000L);
        return ReissueResponseDTO.builder().accessToken(newAccessToken).build();
    }
}
