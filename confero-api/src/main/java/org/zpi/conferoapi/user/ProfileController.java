package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ProfileApi;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.openapitools.model.UpdateProfileInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.security.SecurityUtils;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ProfileController implements ProfileApi {
    ProfileService profileService;
    SecurityUtils securityUtils;

    @Override
    public ResponseEntity<ProfileResponse> getUserProfile() {
        log.info("Got request from user {} to get profile", securityUtils.getCurrentUser());
        return ResponseEntity.ok(profileService.getUserProfile());
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

    @Override
    public ResponseEntity<Void> verifyUserEmail(String token) {
        log.info("Got request to verify email with token: {}", token);
        profileService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}
