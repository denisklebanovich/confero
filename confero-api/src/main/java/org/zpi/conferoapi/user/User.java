package org.zpi.conferoapi.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.zpi.conferoapi.email.UserEmail;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orcid;
    private String accessToken;
    private String avatarUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    @Builder.Default
    private List<UserEmail> emails = new ArrayList<>();

    @NotNull
    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    public User(String orcid, String accessToken) {
        this.orcid = orcid;
        this.accessToken = accessToken;
    }

    public List<String> getEmailList() {
        return emails.stream().map(UserEmail::getEmail).toList();
    }

}