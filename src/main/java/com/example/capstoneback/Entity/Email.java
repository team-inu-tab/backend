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
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String sender;
    private String receiver;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @Column(name = "receive_at")
    private LocalDateTime receiveAt;

    @Column(name="is_draft")
    private Boolean isDraft;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn
    private User user;

    @OneToMany(mappedBy = "email")
    private List<MultiFile> multiFiles = new ArrayList<>();

    @Builder
    public Email(String title, String content, String sender, String receiver, LocalDateTime sendAt, LocalDateTime receiveAt, Boolean isImportant, Boolean isDraft, LocalDateTime scheduledAt, User user) {
        if(sender == null && receiver == null) {
            throw new IllegalArgumentException("Sender and Receiver cannot be null at the same time");
        }
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sendAt = sendAt;
        this.receiveAt = receiveAt;
        this.isImportant = isImportant;
        this.isDraft = isDraft;
        this.scheduledAt = scheduledAt;
        this.user = user;
    }

    // 첫 로그인 후 Gmail 초기화 용도로만 사용
    public void updateCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
