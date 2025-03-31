package com.example.capstoneback.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeleteGmailTemporaryRequestDTO {
    private List<String> selectedMailIds;
}
