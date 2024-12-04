package org.zpi.conferoapi.presentation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    void deleteAllBySession(Session session);

    @Query("""
        SELECT p FROM Presentation p
        JOIN p.presenters pr
        JOIN p.session s
        WHERE 
            ((:orcid IS NOT NULL AND pr.orcid = :orcid)
            OR (:emails IS NOT NULL AND pr.email IN :emails)
            AND (:sessionId IS NULL OR s.id = :sessionId))
    """)
    Set<Presentation> findUserParticipations(@Param("orcid") String orcid, @Param("emails") List<String> emails, @Param("sessionId") Long sessionId);


    default Set<Long> findUserParticipations(User user) {
        var emails = user.getEmailList();
        if(Objects.isNull(user.getOrcid()) && (Objects.isNull(emails) || emails.isEmpty())) {
            return Set.of();
        }

        return findUserParticipations(user.getOrcid(), emails, null).stream()
                .map(Presentation::getId)
                .collect(Collectors.toSet());
    }

    default Set<Presentation> findUserParticipationsWithinSession(User user, Session session) {
        var emails = user.getEmailList();
        if(Objects.isNull(user.getOrcid()) && (Objects.isNull(emails) || emails.isEmpty())) {
            return Set.of();
        }

        return findUserParticipations(user.getOrcid(), emails, session.getId());
    }
}
