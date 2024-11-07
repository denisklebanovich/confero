package org.zpi.conferoapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Profile("!prod")
public class DevAuth extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String email = request.getHeader("Authorization");
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        var newUser = userRepository.save(new User(email));
                        userEmailRepository.save(new UserEmail(email, true, newUser, null));
                        return newUser;
                    });
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getId(), null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }
}
