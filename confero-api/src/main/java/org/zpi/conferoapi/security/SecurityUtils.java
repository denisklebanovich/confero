package org.zpi.conferoapi.security;


import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

    private static Long getCurrentUserId() {
        return (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new ServiceException(ErrorReason.USER_NOT_FOUND));
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser().getIsAdmin();
    }
}
