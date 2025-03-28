package com.example.capstoneback.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HuggingFaceRequestDTO {
    @NotBlank(message = "메세지를 입력해주세요.")
    private String message;
}
