package org.zpi.conferoapi.presentation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.session.Session;

@Getter
@Setter
@Entity
@Table(name = "presenter")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Presenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "presentation_id", nullable = false)
    @ToString.Exclude
    private Presentation presentation;

    @Size(max = 255)
    @NotNull
    @Column(name = "orcid", nullable = false)
    private String orcid;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotNull
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;
}