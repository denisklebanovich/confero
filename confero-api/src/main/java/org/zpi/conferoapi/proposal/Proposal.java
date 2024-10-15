package org.zpi.conferoapi.proposal;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Proposal {
    @Id
    private String id;
    private String title;
    private String content;
    @ManyToMany
    private List<Keyword> keywords;
    @Enumerated(value = EnumType.STRING)
    private ProposalStatus status;
    @Enumerated(value = EnumType.STRING)
    private ProposalType type;

    @ManyToMany
    private List<Presenter> presenters;
}
