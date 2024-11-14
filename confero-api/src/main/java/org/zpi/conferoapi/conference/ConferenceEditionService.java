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
import org.zpi.conferoapi.util.CsvReader;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ConferenceEditionService {

    ConferenceInviteeRepository conferenceInviteeRepository;
    ConferenceEditionRepository conferenceEditionRepository;

    public ConferenceEdition createConferenceEdition(CreateConferenceEdition createConferenceEditionRequest) {
        var editionAcceptingApplications = conferenceEditionRepository.findConferenceEditionAcceptingApplications();

        if (editionAcceptingApplications.isPresent()) {
            throw new ServiceException(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS);
        }

        if (createConferenceEditionRequest.applicationDeadlineTime.isBefore(Instant.now())) {
            throw new ServiceException(ErrorReason.CONFERENCE_EDITION_CANNOT_HAVE_DEADLINE_IN_THE_PAST);
        }

        var newConferenceEdition = conferenceEditionRepository.save(ConferenceEdition.builder()
                .applicationDeadlineTime(createConferenceEditionRequest.getApplicationDeadlineTime())
                .createdAt(Instant.now())
                .build());

        List<ConferenceInvitee> invitees = getInviteesFromInvitationList(newConferenceEdition, createConferenceEditionRequest.getInvitationList());
        newConferenceEdition.addInvitees(invitees);
        return newConferenceEdition;
    }

    public boolean isConferenceEditionAcceptingApplications() {
        return conferenceEditionRepository.findConferenceEditionAcceptingApplications().isPresent();
    }


    public ConferenceEdition updateConferenceEdition(UpdateConferenceEdition updateConferenceEdition) {
        var conferenceEdition = conferenceEditionRepository.findById(updateConferenceEdition.getId())
                .orElseThrow(() -> new ServiceException(ErrorReason.NOT_FOUND));

        updateConferenceEdition.getApplicationDeadlineTime()
                .ifPresent(deadline -> {
                    if (deadline.isBefore(Instant.now())) {
                        throw new ServiceException(ErrorReason.CONFERENCE_EDITION_CANNOT_HAVE_DEADLINE_IN_THE_PAST);
                    }
                    conferenceEdition.setApplicationDeadlineTime(deadline);
                });

        updateConferenceEdition.getInvitationList().ifPresent(file -> {
            List<ConferenceInvitee> newInvitees = getInviteesFromInvitationList(conferenceEdition, Optional.of(file));
            List<ConferenceInvitee> currentInvitees = conferenceEdition.getInvitees();

            for (ConferenceInvitee newInvitee : newInvitees) {
                if (!currentInvitees.contains(newInvitee)) {
                    currentInvitees.add(newInvitee);
                }
            }

            currentInvitees.removeIf(existingUser -> !newInvitees.contains(existingUser));
        });

        return conferenceEditionRepository.save(conferenceEdition);
    }


    public void deleteConferenceEdition(Long conferenceEditionId) {
        var conferenceEdition = conferenceEditionRepository.findById(conferenceEditionId)
                .orElseThrow(() -> new ServiceException(ErrorReason.NOT_FOUND));
        conferenceEditionRepository.delete(conferenceEdition);
    }


    public List<ConferenceEdition> getAllConferenceEditions() {
        return conferenceEditionRepository.findAll();
    }

    private List<ConferenceInvitee> getInviteesFromInvitationList(ConferenceEdition edition, Optional<MultipartFile> invitationList) {
        return invitationList.map(file -> {
            try {
                var emails = CsvReader.readEmailList(file);
                log.info("Read {} emails from the invitation list", emails.size());

                return emails.stream()
                        .map(email ->
                                conferenceInviteeRepository.findByEditionAndIdEmail(edition, email)
                                        .orElseGet(() ->
                                                conferenceInviteeRepository.save(new ConferenceInvitee(edition, email)))
                        )
                        .toList();
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
