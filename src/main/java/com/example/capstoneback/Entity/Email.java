package com.example.capstoneback.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
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
}
