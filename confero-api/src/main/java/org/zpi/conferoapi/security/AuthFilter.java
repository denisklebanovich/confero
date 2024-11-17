package org.zpi.conferoapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final SessionRepository sessionRepository;

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/session") ||
                request.getRequestURI().startsWith("/api/auth/orcid");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String orcidAccessToken = request.getHeader("Orcid-Access-Token");
        String authHeader = request.getHeader("Authorization");

        log.info("doFilterInternal: {}", request.getRequestURI());

        log.info("Auth header: {}", authHeader);
        log.info("Orcid access token: {}", orcidAccessToken);

        boolean authenticated = false;

        if (orcidAccessToken != null) {
            authenticated = handleOrcidAccessToken(orcidAccessToken);
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authenticated = handleJwtAuthentication(authHeader);
        }

        if (!authenticated) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("Unauthorized request from {}", request.getRemoteAddr());
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean handleOrcidAccessToken(String orcidAccessToken) {
        log.info("Handling ORCID access token: {}", orcidAccessToken);
        Long userId = userRepository.findByAccessToken(orcidAccessToken)
                .map(User::getId)
                .orElse(null);

        log.info("User ID: {}", userId);

        if (userId != null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return true;
        } else {
            SecurityContextHolder.clearContext();
            return false;
        }
    }


    private boolean handleJwtAuthentication(String authHeader) {
        String token = authHeader.substring(7);
        log.info("Handling JWT token: {}", token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String email = jwt.getClaim("email").asString();


            log.info("Email: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        var newUser = userRepository.save(
                                User.builder()
                                        .agenda(sessionRepository.findUsersParticipations(null, List.of(email)))
                                        .build()
                        );
                        log.info("New user created: {}", newUser);
                        userEmailRepository.save(new UserEmail(email, true, newUser, null));
                        return newUser;
                    });

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getId(), null, new ArrayList<>()
            );
            authToken.setDetails(jwt.getClaims());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return true;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            return false;
        }
    }


}