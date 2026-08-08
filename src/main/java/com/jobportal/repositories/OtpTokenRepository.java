package com.jobportal.repositories;

import com.jobportal.entities.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByTokenAndEmailAndIsUsedFalse(String token, String email);
    Optional<OtpToken> findByEmailAndIsUsedFalse(String email);
}
