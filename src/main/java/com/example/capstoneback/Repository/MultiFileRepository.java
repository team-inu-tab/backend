package com.example.capstoneback.Repository;

import com.example.capstoneback.Entity.MultiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MultiFileRepository extends JpaRepository<MultiFile, Long> {
    @Query("SELECT m FROM MultiFile m JOIN FETCH m.email e WHERE e.id = :emailId AND m.fileName LIKE CONCAT('%_', :fileName)")
    Optional<MultiFile> findByExactTitleWithEmail(@Param("emailId") Long emailId, @Param("fileName") String fileName);
}
