package com.example.capstoneback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Setter
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "expiration_at", nullable = false)
    private LocalDateTime expirationAt;

    @ManyToOne
    @JoinColumn
    private User user;

    @Builder
    public Token(String refreshToken, LocalDateTime expirationAt, User user) {
        this.refreshToken = refreshToken;
        this.expirationAt = expirationAt;
        this.user = user;
    }
}
