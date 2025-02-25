package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.CustomOAuth2User;
import com.example.capstoneback.DTO.GoogleResponse;
import com.example.capstoneback.DTO.OAuth2Response;
import com.example.capstoneback.DTO.OAuth2UserDTO;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final GmailService gmailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //부모 클래스의 loadUser 생성자를 사용해서 OAuth2User 객체 생성 - 리소스 서버에서 제공해준 데이터를 포함
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        //사용자 요청에서 어떤 인증 방식으로 진행했는지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        // 인증 방식에 따른 상이한 데이터 형식에 대해 분기하여 데이터를 처리
        if(registrationId.equals("google")){
            //인증 방식이 구글일 경우
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            //구글 방식이 아닌 다른 방식일 경우 에러 처리
            System.out.println("invalid registration id");
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid registration id"));
        }

        //스프링 서버에서 사용자를 구별할 수 있는 유저 아이디를 따로 생성(유저 아이디가 겹칠 수 있기 때문에)
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

        //DB에 저장된 유저 정보를 가져옴
        User existData = userRepository.findByUsername(username);

        if(existData == null){ //만약 처음 로그인 한 것이라면

            //새로운 유저 엔티티를 생성하여 데이터를 저장해줌
            User user = User.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .role("ROLE_USER")
                    .build();

            userRepository.save(user);

            // 지메일 메세지 10개 불러오기
            try {
                gmailService.loadGmail(userRequest.getAccessToken(), user);
            } catch (IOException e) {
                System.out.println("loadGmail Runtime Exception");
            }

            //UserDTO에 데이터 저장 후 CustomOAuth2User에 전달하여 결과적으로 OAuth2User를 리턴
            OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2User(userDTO);

        } else { //기존 로그인 정보가 남아있다면

            //이메일과 이름 정보가 수정되었을 수 있으니 기존의 값 업데이트
            existData.updateEmail(oAuth2Response.getEmail());
            existData.updateName(oAuth2Response.getName());

            userRepository.save(existData);

            //처음 로그인과 마찬가지로 DTO를 생성하여 CustomOAuth2User에 전달 후 OAuth2User를 리턴
            OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                    .username(existData.getUsername())
                    .name(oAuth2Response.getName())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDTO);
        }
    }
}
