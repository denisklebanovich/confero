package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.configuration.S3Service;
import org.zpi.conferoapi.email.EmailServiceImpl;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.security.SecurityUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final S3Service s3Service;
    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;
    private final EmailServiceImpl emailService;

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
        var existingEmail = userEmailRepository.findById(updateEmailRequest.getEmail());
        if (existingEmail.isPresent()) {
            throw new ServiceException(ErrorReason.EMAIL_ALREADY_EXISTS);
        }
        String verificationToken = UUID.randomUUID().toString();
        UserEmail userEmail = new UserEmail(updateEmailRequest.getEmail(), false, user, verificationToken);
        try {
            emailService.sendVerificationEmail(updateEmailRequest.getEmail(), generateEmailVerificationLink(verificationToken));
        } catch (Exception e) {
            throw new ServiceException(ErrorReason.EMAIL_SENDING_ERROR);
        }
        userEmailRepository.save(userEmail);
        return userMapper.toDto(userRepository.save(user));
    }

    public void verifyEmail(String token) {
        var userEmail = userEmailRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ServiceException(ErrorReason.NOT_FOUND));
        userEmail.setConfirmed(true);
        userEmailRepository.save(userEmail);
    }

    private String generateEmailVerificationLink(String token) {
        return "<a href=http://localhost:8080/api/profile/email/verify?token=" + token + ">Verify your email</a>";
    }
}
