package org.zpi.conferoapi.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ErrorReason;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;
import org.zpi.conferoapi.util.CsvReader;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ConferenceEditionService {

    ConferenceEditionRepository conferenceEditionRepository;

    UserRepository userRepository;

    public ConferenceEdition createConferenceEdition(CreateConferenceEdition createConferenceEditionRequest) {
        var activeEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeEdition.isPresent()) {
            throw new ServiceException(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS);
        }

        List<User> invitees = getInviteesFromInvitationList(createConferenceEditionRequest.getInvitationList());

        return conferenceEditionRepository.save(ConferenceEdition.builder()
                .applicationDeadlineTime(createConferenceEditionRequest.getApplicationDeadlineTime())
                .createdAt(Instant.now())
                .invitees(invitees)
                .build());
    }

    public boolean isConferenceEditionActive() {
        return conferenceEditionRepository.findActiveEditionConference().isPresent();
    }


    public ConferenceEdition updateConferenceEdition(UpdateConferenceEdition updateConferenceEdition) {
        var conferenceEdition = conferenceEditionRepository.findById(updateConferenceEdition.getId())
                .orElseThrow(() -> new ServiceException(ErrorReason.NOT_FOUND));

        updateConferenceEdition.getApplicationDeadlineTime()
                .ifPresent(conferenceEdition::setApplicationDeadlineTime);

        updateConferenceEdition.getInvitationList().ifPresent(file -> {
            List<User> newInvitees = getInviteesFromInvitationList(Optional.of(file));
            List<User> currentInvitees = new ArrayList<>(conferenceEdition.getInvitees());

            for (User newUser : newInvitees) {
                User existingUser = userRepository.findByEmail(newUser.getEmail())
                        .orElse(userRepository.save(newUser));
                if (!currentInvitees.contains(existingUser)) {
                    currentInvitees.add(existingUser);
                }
            }

            currentInvitees.removeIf(existingUser -> !newInvitees.contains(existingUser));
            conferenceEdition.setInvitees(currentInvitees);
        });

        return conferenceEditionRepository.save(conferenceEdition);
    }


    public List<ConferenceEdition> getAllConferenceEditions() {
        return conferenceEditionRepository.findAll();
    }

    private List<User> getInviteesFromInvitationList(Optional<MultipartFile> invitationList) {
        return invitationList.map(file -> {
            try {
                var emails = CsvReader.readEmailList(file);
                log.info("Read {} emails from the invitation list", emails.size());

                return emails.stream()
                        .map(email -> userRepository.findByEmail(email)
                                .orElseGet(() -> {
                                    log.info("User with email {} does not exist, creating new user", email);
                                    return userRepository.save(User.builder()
                                            .email(email)
                                            .isAdmin(false)
                                            .build());
                                }))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new ServiceException(ErrorReason.INVALID_FILE_FORMAT);
            }
        }).orElseGet(List::of);
    }

    @Value
    @Builder
    public static class CreateConferenceEdition {

        Instant applicationDeadlineTime;

        Optional<MultipartFile> invitationList;
    }


    @Value
    @Builder
    public static class UpdateConferenceEdition {

        Optional<Instant> applicationDeadlineTime;

        Optional<MultipartFile> invitationList;

        Long id;
    }
}
