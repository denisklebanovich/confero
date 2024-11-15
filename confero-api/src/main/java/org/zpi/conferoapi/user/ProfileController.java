package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.ProfileApi;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.openapitools.model.UpdateProfileInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProfileController implements ProfileApi {
    private final ProfileService profileService;


    @Override
    public ResponseEntity<ProfileResponse> getUserProfile() {
        return ResponseEntity.ok(profileService.getUserProfile());
    }

    @Override
    public ResponseEntity<ProfileResponse> updateUserInfo(UpdateProfileInfoRequest updateProfileInfoRequest) {
        return ResponseEntity.ok(profileService.updateUserInfo(updateProfileInfoRequest));
    }

    @Override
    public ResponseEntity<ProfileResponse> updateAvatar(MultipartFile avatarFile) {
        return ResponseEntity.ok((profileService.updateAvatar(avatarFile)));
    }

    @Override
    public ResponseEntity<ProfileResponse> updateUserEmail(UpdateEmailRequest updateEmailRequest) {
        return ResponseEntity.ok(profileService.updateUserEmail(updateEmailRequest));
    }

    @Override
    public ResponseEntity<Void> verifyUserEmail(String token) {
        profileService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}
