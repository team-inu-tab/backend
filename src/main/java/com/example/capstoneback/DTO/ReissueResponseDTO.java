package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueResponseDTO {
    private String accessToken;
    private String refreshToken;

    @Builder
    public ReissueResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
