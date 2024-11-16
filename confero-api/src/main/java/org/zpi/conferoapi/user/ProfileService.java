package org.zpi.conferoapi.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ProfileResponse;
import org.openapitools.model.UpdateEmailRequest;
import org.openapitools.model.UpdateProfileInfoRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.configuration.S3Service;
import org.zpi.conferoapi.email.EmailServiceImpl;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.security.SecurityUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ProfileService {
    UserRepository userRepository;
    UserEmailRepository userEmailRepository;
    S3Service s3Service;
    SecurityUtils securityUtils;
    UserMapper userMapper;
    EmailServiceImpl emailService;
    ConferenceEditionRepository conferenceEditionRepository;

    public ProfileResponse getUserProfile() {
        User user = securityUtils.getCurrentUser();
        return userMapper.toDto(securityUtils.getCurrentUser(), isUserInvitee(user));
    }

    public ProfileResponse updateUserInfo(UpdateProfileInfoRequest updateProfileInfoRequest) {
        User user = securityUtils.getCurrentUser();
        user.setName(updateProfileInfoRequest.getName());
        user.setSurname(updateProfileInfoRequest.getSurname());
        return userMapper.toDto(userRepository.save(user), isUserInvitee(user));
    }


    public ProfileResponse updateAvatar(MultipartFile file) {
        User user = securityUtils.getCurrentUser();
        String key = "avatars/" + user.getId() + "/" + file.getOriginalFilename();
        String url = s3Service.uploadFile(key, file);
        user.setAvatarUrl(url);
        return userMapper.toDto(userRepository.save(user), isUserInvitee(user));
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
        return userMapper.toDto(userRepository.save(user), isUserInvitee(user));
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

    private boolean isUserInvitee(User user) {
        var edition = conferenceEditionRepository.findCurrentConferenceEdition();
        return edition.map(conferenceEdition -> conferenceEdition.getInvitees().stream().anyMatch(i -> user.getEmailList().contains(i.getEmail())))
                .orElse(false);
    }
}
