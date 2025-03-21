package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.ReissueRequestDTO;
import com.example.capstoneback.DTO.ReissueResponseDTO;
import com.example.capstoneback.Jwt.JwtUtil;
import com.example.capstoneback.Service.OAuth2Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JwtUtil jwtUtil;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/oauth2/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = null;

        //요청 쿠키에서 refresh token을 가져옴
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh-token")){
                refreshToken = cookie.getValue();
            }
        }

        //토큰을 dto에 담음
        ReissueRequestDTO requestDTO = ReissueRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        //reissue 메서드 실행
        ReissueResponseDTO responseDTO = oAuth2Service.reissue(requestDTO);

        //검증 실패했다면 401 코드 리턴
        if(responseDTO.getAccessToken() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //검증 성공했다면
        //헤더에 access token 저장 및 리프레시 토큰 쿠키 저장
        //response.setHeader("Authorization", "Bearer " + responseDTO.getAccessToken());
        //response.addCookie(createCookie("refresh-token", responseDTO.getRefreshToken(), 24*60*60, "/"));

        response.setHeader("Authorization", "Bearer " + responseDTO.getAccessToken());
        ResponseCookie refreshCookie = createCookie("refresh-token", responseDTO.getRefreshToken(), 24 * 60 * 60, "/");
        // Set-Cookie 헤더로 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //쿠키 생성 메서드
    public ResponseCookie createCookie(String key, String value, int maxAge, String path) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(Duration.ofSeconds(maxAge))
                .path(path)
                .build();
    }
}
