package org.zpi.conferoapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :email OR u.orcid = :orcid")
    Optional<User> findByEmailOrOrcid(String email, String orcid);

    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email IN :emails")
    Optional<User> findByEmails(List<String> emails);

    Optional<User> findByOrcid(String orcid);

    Optional<User> findByAccessToken(String accessToken);
}
