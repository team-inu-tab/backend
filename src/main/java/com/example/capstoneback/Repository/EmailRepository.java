package com.example.capstoneback.Repository;

import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByUserAndReceiverAndIsDraftIsFalse(User user, String receiver, Limit limit);
    List<Email> findByUserAndSenderAndIsDraftIsFalse(User user, String sender, Limit limit);
}
