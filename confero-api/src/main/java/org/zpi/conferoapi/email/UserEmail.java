package org.zpi.conferoapi.email;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.zpi.conferoapi.user.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserEmail {
    @Id
    private String email;
    private boolean confirmed;
    @ManyToOne(optional = false)
    @ToString.Exclude
    private User user;
    private String verificationToken;
}
