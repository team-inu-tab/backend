package com.example.capstoneback.Repository;

import com.example.capstoneback.Entity.Email;
import com.example.capstoneback.Entity.MultiFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MultiFileRepository extends JpaRepository<MultiFile, Long> {
    Optional<MultiFile> findByEmailAndFileNameContaining(Email email, String fileName);
}
