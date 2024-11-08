package org.zpi.conferoapi.session;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionType;
import org.zpi.conferoapi.application.ApplicationComment;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    @ToString.Exclude
    private User creator;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

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
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Presentation> presentations = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ApplicationComment> comments = new ArrayList<>();


    public Instant getStartTime() {
        if(!isTimeTableConfigured()) {
            return null;
        }
        return  presentations.stream()
                .map(Presentation::getStartTime)
                .min(Instant::compareTo)
                .orElse(null);
    }

    public Instant getEndTime() {
        if(!isTimeTableConfigured()) {
            return null;
        }
        return presentations.stream()
                .map(Presentation::getEndTime)
                .max(Instant::compareTo)
                .orElse(null);
    }

    public boolean isTimeTableConfigured() {
        return presentations.stream()
                .allMatch(presentation -> presentation.getStartTime() != null && presentation.getEndTime() != null);
    }

}