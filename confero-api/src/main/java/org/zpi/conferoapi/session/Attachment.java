package org.zpi.conferoapi.session;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String name;
    private String url;

    @ManyToOne
    private Session session;
}
