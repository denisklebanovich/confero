package org.zpi.conferoapi.user;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String email;
    String orcid;
    String accessToken;
    String avatarUrl;
    @NotNull
    Boolean isAdmin;
}