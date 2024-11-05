package org.zpi.conferoapi.session;

import org.openapitools.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByIdAndCreatorIdAndStatusIsIn(Long id, Long creatorId, List<ApplicationStatus> statuses);
}