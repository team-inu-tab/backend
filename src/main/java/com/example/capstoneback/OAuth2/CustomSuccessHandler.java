package com.example.capstoneback.OAuth2;

import com.example.capstoneback.DTO.CustomOAuth2User;
import com.example.capstoneback.Entity.Token;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Jwt.JwtUtil;
import com.example.capstoneback.Repository.TokenRepository;
import com.example.capstoneback.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${FRONT_ADDRESS}")
    private String FRONT_ADDRESS;

    @Value("${LOGIN_SUCCESS_REDIRECT_PAGE}")
    private String LOGIN_SUCCESS_REDIRECT_PAGE;

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // Refresh Token 수명: 24시간
        String refreshToken = jwtUtil.createJwt("refresh-token" ,username, role, 24*60*60*1000L);

        // 유저 확인
        Optional<User> op_user = userRepository.findByUsername(username);
        if(op_user.isEmpty()){
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        // 생성된 리프레시 토큰 db에 저장
        Token refreshTokenEntity = Token.builder()
                .refreshToken(refreshToken)
                .expirationAt(LocalDateTime.now().plusHours(24))
                .user(op_user.get())
                .build();

        tokenRepository.save(refreshTokenEntity);

        // Refresh Token 쿠키 수명: 24시간, 사용 가능 url path: '/reissue'
        response.addCookie(createCookie("refresh-token", refreshToken, 24*60*60, "/"));

        response.sendRedirect(FRONT_ADDRESS + LOGIN_SUCCESS_REDIRECT_PAGE); //프론트 특정 url로 리다이렉트 되게 설정
    }

    //쿠키 생성 메서드
    public Cookie createCookie(String key, String value, int maxAge, String path){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
//        cookie.setSecure(true); //https일 경우 활성화
        cookie.setPath(path); //쿠키를 사용할 수 있는 패스 설정
        cookie.setHttpOnly(true); // 자바스크립트가 해당 쿠키를 가져가지 못하게 함

        return cookie;
    }
}
