package org.zpi.conferoapi.conference;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConferenceInviteeRepository extends JpaRepository<ConferenceInvitee, InviteeId> {
    Optional<ConferenceInvitee> findByEditionAndIdEmail(ConferenceEdition edition, String email);
}