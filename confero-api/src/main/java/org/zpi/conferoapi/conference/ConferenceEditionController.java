package org.zpi.conferoapi.conference;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ConferenceEditionApi;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ConferenceEditionSummaryResponse;
import org.openapitools.model.ErrorReason;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.conference.ConferenceEditionService.CreateConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionService.UpdateConferenceEdition;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.security.SecurityUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class ConferenceEditionController implements ConferenceEditionApi {

    ConferenceEditionService conferenceEditionService;
    SecurityUtils securityUtils;


    @Override
    public ResponseEntity<ConferenceEditionResponse> createConferenceEdition(Instant applicationDeadlineTime, MultipartFile invitationList) {
        log.info("Got request from user to create conference edition with application deadline time: {} and invitation list: {}",
                applicationDeadlineTime, invitationList);

        if(!securityUtils.isCurrentUserAdmin()) {
            throw new ServiceException(ErrorReason.FORBIDDEN);
        }

        var created = conferenceEditionService.createConferenceEdition(
                CreateConferenceEdition.builder()
                        .invitationList(Optional.ofNullable(invitationList))
                        .applicationDeadlineTime(applicationDeadlineTime)
                        .build()
        );

        log.info("Created conference edition: {}", created);

        return new ResponseEntity<>(
                new ConferenceEditionResponse()
                        .id(created.getId())
                        .applicationDeadlineTime(created.getApplicationDeadlineTime())
                        .numberOfInvitations(created.getInvitees().size())
                        .createdAt(created.getCreatedAt()),
                CREATED);
    }

    @Override
    public ResponseEntity<ConferenceEditionResponse> updateConferenceEdition(Long conferenceEditionId, Instant applicationDeadlineTime, MultipartFile invitationList) {
        log.info("Got request from user to update conference edition with id: {} to application deadline time: {} and invitation list: {}",
                conferenceEditionId, applicationDeadlineTime, invitationList);

        if(!securityUtils.isCurrentUserAdmin()) {
            throw new ServiceException(ErrorReason.FORBIDDEN);
        }

        var updated = conferenceEditionService.updateConferenceEdition(
                UpdateConferenceEdition.builder()
                        .id(conferenceEditionId)
                        .applicationDeadlineTime(Optional.ofNullable(applicationDeadlineTime))
                        .invitationList(Optional.ofNullable(invitationList))
                        .build()
        );

        log.info("Updated conference edition: {}", updated);

        return new ResponseEntity<>(
                new ConferenceEditionResponse()
                        .id(updated.getId())
                        .applicationDeadlineTime(updated.getApplicationDeadlineTime())
                        .numberOfInvitations(updated.getInvitees().size())
                        .createdAt(updated.getCreatedAt()),
                OK);
    }

    @Override
    public ResponseEntity<List<ConferenceEditionResponse>> getAllConferenceEditions() {
        log.info("Got request from user to get all conference editions");

        if(!securityUtils.isCurrentUserAdmin()) {
            throw new ServiceException(ErrorReason.FORBIDDEN);
        }

        var editions = conferenceEditionService.getAllConferenceEditions().stream().map(edition -> new ConferenceEditionResponse()
                .id(edition.getId())
                .applicationDeadlineTime(edition.getApplicationDeadlineTime())
                .numberOfInvitations(edition.getInvitees().size())
                .createdAt(edition.getCreatedAt())
        ).toList();

        log.info("Returning {} conference editions ", editions.size());
        return ResponseEntity.ok(editions);
    }

    @Override
    public ResponseEntity<ConferenceEditionSummaryResponse> getConferenceEditionSummary() {
        log.info("Got request from user to get conference edition summary");
        var activeEdition = conferenceEditionService.isConferenceEditionActive();

        var summary = new ConferenceEditionSummaryResponse()
                .acceptingNewApplications(activeEdition);

        log.info("Returning conference edition summary: {}", summary);
        return ResponseEntity.ok(summary);
    }


    @Override
    public ResponseEntity<Void> deleteConferenceEdition(Long conferenceEditionId) {
        log.info("Got request from user to delete conference edition with id: {}", conferenceEditionId);
        if(!securityUtils.isCurrentUserAdmin()) {
            throw new ServiceException(ErrorReason.FORBIDDEN);
        }
        conferenceEditionService.deleteConferenceEdition(conferenceEditionId);
        return ResponseEntity.ok().build();
    }
}
