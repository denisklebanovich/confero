package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ProfileApi;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.openapitools.model.UpdateProfileInfoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.zpi.conferoapi.security.SecurityUtils;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ProfileController implements ProfileApi {
    ProfileService profileService;
    SecurityUtils securityUtils;

    @Value("${redirect.base-url}")
    String redirectBaseUrl;

    @Override
    public ResponseEntity<ProfileResponse> getUserProfile() {
        long startTime = System.currentTimeMillis(); // Record start time
        log.info("Got request from user {} to get profile", securityUtils.getCurrentUser());
        var resp =  ResponseEntity.ok(profileService.getUserProfile());
        long endTime = System.currentTimeMillis(); // Record end time
        log.info("Execution time for getUserProfile: {} ms", (endTime - startTime));
        return resp;
    }

    @Override
    public ResponseEntity<ProfileResponse> updateUserInfo(UpdateProfileInfoRequest updateProfileInfoRequest) {
        log.info("Got request from user {} to update profile info with request: {}", securityUtils.getCurrentUser(), updateProfileInfoRequest);
        return ResponseEntity.ok(profileService.updateUserInfo(updateProfileInfoRequest));
    }

    @Override
    public ResponseEntity<ProfileResponse> updateAvatar(MultipartFile avatarFile) {
        log.info("Got request from user {} to update avatar with file: {}", securityUtils.getCurrentUser(), avatarFile);
        return ResponseEntity.ok((profileService.updateAvatar(avatarFile)));
    }

    @Override
    public ResponseEntity<ProfileResponse> updateUserEmail(UpdateEmailRequest updateEmailRequest) {
        log.info("Got request from user {} to update email with request: {}", securityUtils.getCurrentUser(), updateEmailRequest);
        return ResponseEntity.ok(profileService.updateUserEmail(updateEmailRequest));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/profile/email/verify",
            produces = { "application/json" }
    )
    public RedirectView verifyUserEmail(@RequestParam String token) {
        log.info("Got request to verify email with token: {}", token);
        profileService.verifyEmail(token);
        return new RedirectView(redirectBaseUrl + "/profile");
    }

    @Override
    public ResponseEntity<ProfileResponse> verifyOrcid(String orcid, String accessToken) {
        return ResponseEntity.ok(profileService.verifyOrcid(orcid, accessToken));
    }
}
