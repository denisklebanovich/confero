package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.configuration.S3Service;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.security.SecurityUtils;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;

    public ProfileResponse getUserProfile() {
        return userMapper.toDto(securityUtils.getCurrentUser());
    }


    public ProfileResponse updateAvatar(MultipartFile file) {
        User user = securityUtils.getCurrentUser();
        String key = "avatars/" + user.getId() + "/" + file.getOriginalFilename();
        String url = s3Service.uploadFile(key, file);
        user.setAvatarUrl(url);
        return userMapper.toDto(userRepository.save(user));
    }


    public ProfileResponse updateUserEmail(UpdateEmailRequest updateEmailRequest) {
        User user = securityUtils.getCurrentUser();
        if (user.getOrcid() == null) {
            throw new ServiceException(ErrorReason.EMAIL_CANNOT_BE_UPDATED);
        }
        user.setEmail(updateEmailRequest.getEmail());
        user.setEmailVerified(false);
        return userMapper.toDto(userRepository.save(user));
    }
}
