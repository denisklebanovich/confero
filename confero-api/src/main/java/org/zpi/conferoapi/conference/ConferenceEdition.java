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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "application_deadline_time", nullable = false)
    private Instant applicationDeadlineTime;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}