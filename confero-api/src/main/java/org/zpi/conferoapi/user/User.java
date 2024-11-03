package org.zpi.conferoapi.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String orcid;
    private String accessToken;

    @NotNull
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    public User(String orcid, String accessToken) {
        this.orcid = orcid;
        this.accessToken = accessToken;
    }

    public User(String email){
        this.email = email;
    }
}