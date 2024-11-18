package org.zpi.conferoapi.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.zpi.conferoapi.user.User;

import java.net.URI;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/auth/orcid")
@RequiredArgsConstructor
public class OrcidAuthController {
    private final OrcidAuthService orcidAuthService;

    @Value("${redirect.base-url}")
    private String baseUrl;

    @GetMapping("/login")
    public RedirectView loginWithOrcid(@RequestParam boolean verify) {
        String authorizationUrl = orcidAuthService.getAuthorizationUrl(verify);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> orcidCallback(@RequestParam String code) {
        log.info("Received ORCID callback with code {}", code);
        try {
            User user = orcidAuthService.authorizeUser(code);
            log.info("User {} authorized", user);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(),
                    null, Collections.emptyList()));

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("%s/login?orcid_access_token=%s",
                            baseUrl, user.getAccessToken())))
                    .build();
        } catch (Exception e) {
            log.info("Error while orcid callback", e);
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/callback/verify")
    public ResponseEntity<Void> verifyOrcid(@RequestParam String code) {
        log.info("Received ORCID verify callback with code {}", code);
        try {
            Pair<String, String> response = orcidAuthService.verifyUser(code);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("%s/profile?orcid=%s&accessToken=%s",
                            baseUrl, response.a, response.b)))
                    .build();
        } catch (Exception e) {
            log.info("Error while orcid verify callback", e);
            return ResponseEntity.status(403).build();
        }
    }
}
