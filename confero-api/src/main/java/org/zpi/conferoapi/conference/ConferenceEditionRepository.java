package org.zpi.conferoapi.conference;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceEditionRepository extends JpaRepository<ConferenceEdition, Long> {

    @Query(value = """
            SELECT * FROM conference_edition ce WHERE ce.application_deadline_time > CURRENT_TIMESTAMP LIMIT 1"""
            , nativeQuery = true)
    Optional<ConferenceEdition> findConferenceEditionAcceptingApplications();

    @Query(value = """
        SELECT * FROM conference_edition ce ORDER BY ce.application_deadline_time DESC LIMIT 1
    """, nativeQuery = true)
    Optional<ConferenceEdition> findCurrentConferenceEdition();

    @NotNull Optional<ConferenceEdition> findById(@NotNull Long id);
}