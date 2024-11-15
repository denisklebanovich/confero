package org.zpi.conferoapi.auth;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/auth/orcid")
@RequiredArgsConstructor
public class OrcidAuthController {
    private final OrcidAuthService orcidAuthService;

    @Value("${redirect.base-url}")
    private String baseUrl;

    @GetMapping("/login")
    public RedirectView loginWithOrcid() {
        String authorizationUrl = orcidAuthService.getAuthorizationUrl();
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> orcidCallback(@RequestParam String code) {
        try {
            User user = orcidAuthService.authorizeUser(code);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(),
                    null, Collections.emptyList()));

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("%s/login?orcid_access_token=%s",
                            baseUrl, user.getAccessToken())))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
}
