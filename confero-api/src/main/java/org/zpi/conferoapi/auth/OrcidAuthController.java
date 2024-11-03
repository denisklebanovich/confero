package org.zpi.conferoapi.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth/orcid")
@RequiredArgsConstructor
public class OrcidAuthController {
    private final OrcidAuthService orcidAuthService;

    @GetMapping("/login")
    public RedirectView loginWithOrcid() {
        String authorizationUrl = orcidAuthService.getAuthorizationUrl();
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> orcidCallback(@RequestParam String code) {
        try {
            Map<String, Object> tokenResponse = orcidAuthService.getAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(String.format("http://localhost:5173/login?orcid_access_token=%s", accessToken)))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
}
