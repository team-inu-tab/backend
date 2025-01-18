package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueRequestDTO {
    private String refreshToken;

    @Builder
    public ReissueRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
