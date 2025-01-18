package com.example.capstoneback.Config;

import com.example.capstoneback.Jwt.JwtFilter;
import com.example.capstoneback.Jwt.JwtUtil;
import com.example.capstoneback.OAuth2.CustomAccessDeniedHandler;
import com.example.capstoneback.OAuth2.CustomAuthenticationEntryPoint;
import com.example.capstoneback.OAuth2.CustomSuccessHandler;
import com.example.capstoneback.Service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/reissue").permitAll()
                        .anyRequest().authenticated());

        //CORS 설정
        http
                .cors((corsCustomizer) -> corsCustomizer
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                //요청 허용하는 오리진 설정
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5500"));

                                //허용하는 요청 종류(Get, Post...) 설정
                                configuration.setAllowedMethods(Collections.singletonList("*"));

                                //서버와 다른 오리진에서는 credential 설정을 하지 않으면 Authorization 헤더를 포함하여 전송할 수가 없음
                                configuration.setAllowCredentials(true);

                                //모든 헤더를 받을 수 있도록 허용
                                configuration.setAllowedHeaders(Collections.singletonList("*"));

                                //최초 CORS 인증 후 유효 시간(24시간)
                                configuration.setMaxAge(20*60*60L);

                                //서버에서 보낸 응답 헤더 중에 클라이언트가 접근 가능한 헤더를 설정
                                configuration.setExposedHeaders(Arrays.asList("Authorization"));

                                return configuration;
                            }
                        }));

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        //커스텀한 OAuth2UserService를 사용하겠다는 설정
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customSuccessHandler) // SuccessHandler 설정
                )
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 받지 않은 유저 예외 처리
                        .accessDeniedHandler(customAccessDeniedHandler) // 인가 받지 않은 유저 예외 처리
                );

        //JwtFilter 추가
        http
                .addFilterBefore(new JwtFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        //무상태성으로 세션 관리 - jwt 토큰 사용
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
