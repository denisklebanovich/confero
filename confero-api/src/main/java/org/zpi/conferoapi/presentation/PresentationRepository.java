package org.zpi.conferoapi.presentation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zpi.conferoapi.session.Session;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    void deleteAllBySession(Session session);
}
