package org.zpi.conferoapi.conference;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ConferenceInviteeId implements Serializable {
    @NotNull
    @Column(name = "user_id", nullable = false, length = Integer.MAX_VALUE)
    private Long userId;

    @NotNull
    @Column(name = "edition_id", nullable = false)
    private Long editionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ConferenceInviteeId entity = (ConferenceInviteeId) o;
        return Objects.equals(this.editionId, entity.editionId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(editionId, userId);
    }

}