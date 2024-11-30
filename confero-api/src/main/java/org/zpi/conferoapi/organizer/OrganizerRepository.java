package org.zpi.conferoapi.organizer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zpi.conferoapi.presentation.Presenter;

import java.util.Set;

public interface OrganizerRepository extends JpaRepository<Presenter, Long> {

    @Query("""
        SELECT DISTINCT p
        FROM Presenter p
        JOIN Presentation pr ON p.presentation.id = pr.id
        JOIN Session s ON pr.session.id = s.id
        JOIN ConferenceEdition ce ON s.edition.id = ce.id
        WHERE ce.applicationDeadlineTime > CURRENT_TIMESTAMP
        AND s.status = 'ACCEPTED'
          AND (
              p.name LIKE %:searchQuery%
              OR p.surname LIKE %:searchQuery%
              OR p.orcid LIKE %:searchQuery%
          )
        """)
    Set<Presenter> findOrganizers(String searchQuery);
}
