package com.example.capstoneback.Jwt;

import com.example.capstoneback.DTO.CustomOAuth2User;
import com.example.capstoneback.DTO.OAuth2UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        String accessToken = null;

        //요청 헤더에 Authorization key가 있는지, 있다면 Bearer로 시작하는지 확인
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
        }else{
            System.out.println("accessToken is null");
            filterChain.doFilter(request, response);
            return;
        }

        //토큰이 만료 및 올바른 형식의 토큰인지 확인
        try{
            jwtUtil.isExpired(accessToken);
        }catch (ExpiredJwtException e){
            System.out.println("token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch(MalformedJwtException | IllegalArgumentException e){
            System.out.println("invalid JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰의 category가 access-token이 아닐 경우 401 코드 리턴
        if(!jwtUtil.getCategory(accessToken).equals("access-token")){
            System.out.println("invalid token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String role = jwtUtil.getRole(accessToken);
        String name = jwtUtil.getUsername(accessToken);

        OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                .role(role)
                .name(name)
                .build();

        //유저 정보 객체 생성
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //생성한 유저 정보 객체를 바탕으로 유저 인증 객체 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //SecurityContext에 유저 인증 정보 일시적으로 저장(응답을 보낸 후 세션 삭제됨)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //필터 체인의 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}
