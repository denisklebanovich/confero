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
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final SessionRepository sessionRepository;

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String orcidAccessToken = request.getHeader("Orcid-Access-Token");
        if (orcidAccessToken != null) {
            Long userId = userRepository.findByAccessToken(orcidAccessToken)
                    .map(User::getId)
                    .orElseGet(() -> {
                        SecurityContextHolder.clearContext();
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return null;
                    });

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(token);
                String email = jwt.getClaim("email").asString();

                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            var newUser = userRepository.save(new User(sessionRepository.findUsersParticipations(null, List.of(email))));
                            userEmailRepository.save(new UserEmail(email, true, newUser, null));
                            return newUser;
                        });

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.getId(), null, new ArrayList<>()
                );
                authToken.setDetails(jwt.getClaims());

                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
}