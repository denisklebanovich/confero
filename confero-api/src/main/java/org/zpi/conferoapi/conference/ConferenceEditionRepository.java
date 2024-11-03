package org.zpi.conferoapi.conference;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceEditionRepository extends JpaRepository<ConferenceEdition, Long> {

    @Query("SELECT ce FROM ConferenceEdition ce WHERE ce.applicationDeadlineTime > CURRENT_TIMESTAMP")
    Optional<ConferenceEdition> findActiveEditionConference();

    @NotNull Optional<ConferenceEdition> findById(@NotNull Long id);
}
