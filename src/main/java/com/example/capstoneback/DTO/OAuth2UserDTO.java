package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2UserDTO {
    private String role;
    private String username;
    private String name;

    @Builder
    public OAuth2UserDTO(String role, String username, String name) {
        this.role = role;
        this.username = username;
        this.name = name;
    }
}
