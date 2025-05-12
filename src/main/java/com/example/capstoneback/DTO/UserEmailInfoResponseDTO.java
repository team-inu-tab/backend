package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserEmailInfoResponseDTO {
    private String email;

    @Builder
    public UserEmailInfoResponseDTO(String email) {
        this.email = email;
    }
}
