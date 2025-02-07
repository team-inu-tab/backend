package com.example.capstoneback.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String studentDepartment;
    private Integer studentNum;

    //직장인 추가정보
    private String workerDepartment;
    private String company;
    private String position;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens = new ArrayList<>();

    @Builder
    public User(String name, String email, String role, String username) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStudentInfo(String studentDepartment, Integer studentNum){
        this.studentDepartment = studentDepartment;
        this.studentNum = studentNum;
    }

    public void updateWorkerInfo(String workerDepartment, String company, String position){
        this.workerDepartment = workerDepartment;
        this.company = company;
        this.position = position;
    }
}
