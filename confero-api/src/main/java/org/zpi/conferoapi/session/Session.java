package org.zpi.conferoapi.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zpi.conferoapi.proposal.Presenter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue
    private Long id;

    private Integer duration;
    private String title;
    private String description;
    @ManyToOne
    private Presenter presenter;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String streamUrl;

    @OneToMany
    private List<Attachment> attachments;
}
