package org.zpi.conferoapi.security;


import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public static Long getCurrentUserId() {
        return (long) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    // Log or inspect the principal value
                    System.out.println("Principal: " + principal + " (Type: " + (principal != null ? principal.getClass() : "null") + ")");
                    if (principal instanceof Long) {
                        return (Long) principal;
                    } else if (principal instanceof String) {
                        return Long.valueOf((String) principal);
                    } else {
                        throw new ServiceException(ErrorReason.UNAUTHORIZED);
                    }
                })
                .orElseThrow(() -> new ServiceException(ErrorReason.UNAUTHORIZED));
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new ServiceException(ErrorReason.USER_NOT_FOUND));
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser().getIsAdmin();
    }
}
