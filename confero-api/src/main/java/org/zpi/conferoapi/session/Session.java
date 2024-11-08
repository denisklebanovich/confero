package org.zpi.conferoapi.session;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionType;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "session")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Session {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "type", nullable = false)
    private SessionType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    @ToString.Exclude
    private User creator;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> tags;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "edition_id", nullable = false)
    @ToString.Exclude
    private ConferenceEdition edition;

    @Size(max = 40000)
    @NotNull
    @Column(name = "description", nullable = false, length = 40000)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Presentation> presentations = new ArrayList<>();
}