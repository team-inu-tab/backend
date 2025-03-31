package com.example.capstoneback.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeleteGmailPermanentRequestDTO {
    private List<String> selectedMailIds;
}
