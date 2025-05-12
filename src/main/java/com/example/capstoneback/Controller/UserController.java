package com.example.capstoneback.Controller;

import com.example.capstoneback.DTO.UserEmailInfoResponseDTO;
import com.example.capstoneback.DTO.UserInfoDTO;
import com.example.capstoneback.Error.NotValidArgumentException;
import com.example.capstoneback.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/info/student")
    public ResponseEntity<Void> storeUserInfoForStudent(@Valid @RequestBody UserInfoDTO.Student studentInfo, Errors errors, HttpServletRequest request) {

        if(errors.hasErrors()) {
            throw new NotValidArgumentException("not valid argument", errors);
        }

        userService.storeUserInfoForStudent(studentInfo, request);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/users/info/worker")
    public ResponseEntity<Void> storeUserInfoForWorker(@Valid @RequestBody UserInfoDTO.Worker workerInfo, Errors errors, HttpServletRequest request) {

        if(errors.hasErrors()) {
            throw new NotValidArgumentException("not valid argument", errors);
        }

        userService.storeUserInfoForWorker(workerInfo, request);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/info/email")
    public ResponseEntity<UserEmailInfoResponseDTO> getUserEmail(HttpServletRequest request) {
        UserEmailInfoResponseDTO responseDTO = userService.getUserEmail(request);
        return ResponseEntity.ok(responseDTO);
    }
}
