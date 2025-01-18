package com.example.capstoneback.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueResponseDTO {
    private String accessToken;

    @Builder
    public ReissueResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
