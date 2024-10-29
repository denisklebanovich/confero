package org.zpi.conferoapi.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private String email;

    @NotNull
    @ColumnDefault("false")
    private Boolean isAdmin = false;

}