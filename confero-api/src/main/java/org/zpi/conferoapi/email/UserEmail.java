package org.zpi.conferoapi.email;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.zpi.conferoapi.user.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEmail {
    @Id
    private String email;
    private boolean confirmed;
    @ManyToOne(optional = false)
    private User user;
    private String verificationToken;
}
