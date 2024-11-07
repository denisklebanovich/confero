package org.zpi.conferoapi.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zpi.conferoapi.email.UserEmail;

import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String orcid;
    private String accessToken;
    private String avatarUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserEmail> emails;

    @NotNull
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    public User(String orcid, String accessToken) {
        this.orcid = orcid;
        this.accessToken = accessToken;
    }

    public User(String email) {
        this.email = email;
    }
}