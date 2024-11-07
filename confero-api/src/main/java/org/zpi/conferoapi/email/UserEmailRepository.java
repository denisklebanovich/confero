package org.zpi.conferoapi.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmailRepository extends JpaRepository<UserEmail, String> {
    Optional<UserEmail> findByVerificationToken(String token);
}