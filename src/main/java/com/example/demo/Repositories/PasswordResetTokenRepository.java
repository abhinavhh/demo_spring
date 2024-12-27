package com.example.demo.Repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
