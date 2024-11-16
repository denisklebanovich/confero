package org.zpi.conferoapi.conference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conference_invitee")
@NoArgsConstructor
@Getter
@Setter
public class ConferenceInvitee {
    @EmbeddedId
    private InviteeId id;

    public ConferenceInvitee(ConferenceEdition edition, String email) {
        this.id = new InviteeId(edition.getId(), email);
        this.edition = edition;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edition_id", insertable = false, updatable = false)
    private ConferenceEdition edition;


    public String getEmail() {
        return id.getEmail();
    }

}
