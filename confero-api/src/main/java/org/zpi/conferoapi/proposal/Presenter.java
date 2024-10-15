package org.zpi.conferoapi.proposal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Presenter {
    @Id
    private String orcid;
    private String name;
    private String surname;
}
