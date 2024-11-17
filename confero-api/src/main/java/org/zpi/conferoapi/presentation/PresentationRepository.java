package org.zpi.conferoapi.presentation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    void deleteAllBySession(Session session);

    @Query("""
            SELECT p.id FROM Presentation p
            JOIN p.presenters pr
            WHERE (:orcid IS NULL OR pr.orcid = :orcid)
            OR (:emails IS NULL OR pr.email IN :emails)
        """)
    Set<Long> findUserParticipations(@Param("orcid") String orcid, @Param("emails") List<String> emails);


    default Set<Long> findUserParticipations(User user) {
        var emails = user.getEmailList();
        if(Objects.isNull(user.getOrcid()) && (Objects.isNull(emails) || emails.isEmpty())) {
            return Set.of();
        }

        return findUserParticipations(user.getOrcid(), emails);
    }
}
