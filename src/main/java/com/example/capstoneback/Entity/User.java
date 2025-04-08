package com.example.capstoneback.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String username;

    //학생 추가정보
    private String schoolName;
    private String studentDepartment;
    private Integer studentNum;
    private String studentName;
    private String studentGrade;

    //직장인 추가정보
    private String workerDepartment;
    private String company;
    private String position;

    //access token
    private String accessToken;

    //전화번호
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Email> emails = new ArrayList<>();

    @Builder
    public User(String name, String email, String role, String username, String accessToken) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
        this.accessToken = accessToken;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public void updateStudentInfo(String schoolName, String studentDepartment, Integer studentNum,
                                  String studentName, String studentGrade, String phoneNumber){
        this.schoolName = schoolName;
        this.studentDepartment = studentDepartment;
        this.studentNum = studentNum;
        this.studentName = studentName;
        this.studentGrade = studentGrade;
        this.phoneNumber = phoneNumber;
    }

    public void updateWorkerInfo(String workerDepartment, String company, String position){
        this.workerDepartment = workerDepartment;
        this.company = company;
        this.position = position;
    }
}
