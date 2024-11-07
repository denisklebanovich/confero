package org.zpi.conferoapi.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zpi.conferoapi.security.SecurityUtils;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrcidAuthService {
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Value("${orcid.client-id}")
    private String clientId;

    @Value("${orcid.client-secret}")
    private String clientSecret;

    @Value("${orcid.redirect-uri}")
    private String redirectUri;

    @Value("${orcid.auth-url}")
    private String authUrl;

    @Value("${orcid.token-url}")
    private String tokenUrl;

    public String getAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl(authUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "/authenticate")
                .queryParam("redirect_uri", redirectUri)
                .toUriString();
    }

    public User authorizeUser(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        var response = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);

        String orcid = (String) response.getBody().get("orcid");
        String accessToken = (String) response.getBody().get("access_token");
        if (SecurityUtils.getCurrentUserId() != null) {
            User currentUser = securityUtils.getCurrentUser();
            currentUser.setOrcid(orcid);
            currentUser.setAccessToken(accessToken);
            userRepository.save(currentUser);
        }
        return userRepository.findByOrcid(orcid).orElseGet(() -> {
            User newUser = new User(orcid, accessToken);
            return userRepository.save(newUser);
        });
    }
}
