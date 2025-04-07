package com.example.capstoneback.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserInfoDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    // 학생 정보를 입력받을 때 쓸 DTO => UserInfoDTO.Student
    public static class Student{
        @NotBlank(message = "학교를 입력해주세요.")
        private String schoolName;

        @NotBlank(message = "학과를 입력해주세요.")
        private String studentDepartment;

        @NotNull(message = "학번을 입력해주세요.")
        @Positive(message = "양수를 입력해주세요.")
        private Integer studentNum;

        @NotNull(message = "학생 이름을 입력해주세요.")
        private String studentName;

        @NotNull(message = "전화번호를 입력해주세요.")
        private String phoneNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    // 직장 정보를 입력받을 때 쓸 DTO => UserInfoDTO.Worker
    public static class Worker {
        @NotBlank(message = "회사명을 입력해주세요.")
        private String company;

        @NotBlank(message = "부서를 입력해주세요.")
        private String workerDepartment;

        @NotBlank(message = "직책을 입력해주세요.")
        private String position;
    }
}
