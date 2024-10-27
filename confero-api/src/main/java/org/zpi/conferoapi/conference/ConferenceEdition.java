package org.zpi.conferoapi.conference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "conference_edition")
public class ConferenceEdition {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Instant applicationDeadlineTime;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant createdAt;

}