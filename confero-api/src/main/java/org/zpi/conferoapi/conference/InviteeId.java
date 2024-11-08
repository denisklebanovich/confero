package org.zpi.conferoapi.conference;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class InviteeId {
    @Column(name = "edition_id")
    private Long editionId;
    @Column(name = "email")
    private String email;
}
