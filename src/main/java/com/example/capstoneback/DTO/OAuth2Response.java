package com.example.capstoneback.DTO;

public interface OAuth2Response {
    //OAuth 제공자마다 전달해주는 사용자 정보 형식이 다르므로 통일화된 처리를 지원하는 인터페이스

    //제공자 (ex. naver, google)
    String getProvider();

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    //사용자 이메일
    String getEmail();

    //사용자 이름(설정한 이름)
    String getName();
}
