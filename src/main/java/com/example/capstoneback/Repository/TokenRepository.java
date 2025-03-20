package com.example.capstoneback.Repository;

import com.example.capstoneback.Entity.Token;
import com.example.capstoneback.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByRefreshToken(String refreshToken);
    boolean existsByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    Optional<Token> findTopByUserOrderByIdDesc(User user);
}
