package org.zpi.conferoapi.presentation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.zpi.conferoapi.session.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "presentation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 12000)
    @Column(name = "description", length = 12000)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private Session session;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @OneToMany(mappedBy = "presentation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Presenter> presenters = new ArrayList<>();

    public Optional<Instant> startTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<Instant> endTime() {
        return Optional.ofNullable(endTime);
    }

    public List<Attachment> getAttachments() {
        return presenters.stream()
                .map(Presenter::getAttachments)
                .flatMap(List::stream)
                .toList();
    }

}