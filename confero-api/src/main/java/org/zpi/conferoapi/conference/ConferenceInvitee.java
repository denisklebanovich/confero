package org.zpi.conferoapi.conference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.zpi.conferoapi.user.User;

@Getter
@Setter
@Entity
@Table(name = "conference_invitee")
public class ConferenceInvitee {
    @EmbeddedId
    private ConferenceInviteeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("editionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "edition_id", nullable = false)
    private ConferenceEdition edition;

}