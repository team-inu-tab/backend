package com.example.capstoneback.Repository;

import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByUserAndReceiverAndIsDraftIsFalseOrderByReceiveAtDesc(User user, String receiver, Limit limit);
    List<Email> findByUserAndSenderAndIsDraftIsFalseOrderBySendAtDesc(User user, String sender, Limit limit);
    List<Email> findByUserAndReceiverAndSenderAndIsDraftIsFalseOrderBySendAtDesc(User user, String receiver, String sender, Limit limit);

    @Query("SELECT e FROM Email e where e.user = :user and e.isImportant = true ORDER BY COALESCE(e.sendAt, e.receiveAt, e.createdAt) DESC")
    List<Email> findByUserAndIsImportantIsTrueLimit10(@Param("user") User user, Limit limit);

    List<Email> findByUserAndScheduledAtIsAfterOrderByScheduledAtDesc(User user, LocalDateTime scheduledAt);
    List<Email> findByUserAndIsDraftIsTrueOrderByCreatedAtDesc(User user, Limit limit);
}
