package org.zpi.conferoapi.proposal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Keyword {
    @Id
    private String value;
}
