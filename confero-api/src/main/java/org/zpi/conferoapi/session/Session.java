package org.zpi.conferoapi.session;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.zpi.conferoapi.proposal.Presenter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Session {
    @Id
    private String id;

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
