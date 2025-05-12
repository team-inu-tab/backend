package com.example.capstoneback.Service;

import com.example.capstoneback.DTO.UserEmailInfoResponseDTO;
import com.example.capstoneback.DTO.UserInfoDTO;
import com.example.capstoneback.Entity.User;
import com.example.capstoneback.Error.ErrorCode;
import com.example.capstoneback.Error.UserDoesntExistException;
import com.example.capstoneback.Jwt.JwtUtil;
import com.example.capstoneback.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //학생 부가 정보 저장 메서드
    @Transactional
    public void storeUserInfoForStudent(UserInfoDTO.Student studentInfo, HttpServletRequest request) {

        User user = getUserFromHeaderToken(request);

        //유저 정보 업데이트
        user.updateStudentInfo(studentInfo.getSchoolName(), studentInfo.getStudentDepartment(), studentInfo.getStudentNum(),
                studentInfo.getStudentName(), studentInfo.getStudentGrade(), studentInfo.getPhoneNumber());
        userRepository.save(user);
    }

    //직장인 부가 정보 저장 메서드
    @Transactional
    public void storeUserInfoForWorker(UserInfoDTO.Worker workerInfo, HttpServletRequest request) {

        User user = getUserFromHeaderToken(request);

        //유저 정보 없데이트
        user.updateWorkerInfo(workerInfo.getWorkerDepartment(), workerInfo.getCompany(), workerInfo.getPosition());
        userRepository.save(user);
    }

    // 유저 이메일 반환 메서드
    public UserEmailInfoResponseDTO getUserEmail(HttpServletRequest request) {
        User user = getUserFromHeaderToken(request);

        return UserEmailInfoResponseDTO.builder()
                .email(user.getEmail())
                .build();
    }

    //request header의 token으로부터 유저 엔티티 가져오는 메서드
    public User getUserFromHeaderToken(HttpServletRequest request) {
        //헤더에서 토큰 분리
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        //username 추출
        String username = jwtUtil.getUsername(token);

        //db에서 유저 정보 가져옴
        Optional<User> user = userRepository.findByUsername(username);

        //유저가 존재하지 않을 경우 에러 발생
        if(user.isEmpty()) {
            throw new UserDoesntExistException(ErrorCode.USER_DOESNT_EXIST);
        }

        return user.get();
    }


}
