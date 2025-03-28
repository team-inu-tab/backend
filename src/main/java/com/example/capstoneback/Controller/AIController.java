package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.HuggingFaceRequestDTO;
import com.example.capstoneback.Error.NotValidArgumentException;
import com.example.capstoneback.Service.HuggingFaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AIController {

    private final HuggingFaceService huggingFaceService;

    @PostMapping("ai/huggingface")
    public ResponseEntity<String> getDataFromHuggingFace(@Valid @RequestBody HuggingFaceRequestDTO requestDTO, Errors errors) {

        if(errors.hasErrors()) {
            throw new NotValidArgumentException("not valid argument", errors);
        }

        String responseData = huggingFaceService.getDataFromHuggingFace(requestDTO);
        return ResponseEntity.ok(responseData);
    }
}
