package com.example.capstoneback.Entity;

import jakarta.persistence.*;
import lombok.Builder;

public class SpamEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spam_email", nullable = false)
    private String spamEmail;

    @ManyToOne
    @JoinColumn
    private User user;

    @Builder
    public SpamEmail(String spamEmail, User user) {
        this.spamEmail = spamEmail;
        this.user = user;
    }
}
