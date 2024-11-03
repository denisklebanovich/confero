package org.zpi.conferoapi.conference;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ConferenceEditionApi;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ConferenceEditionSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.conference.ConferenceEditionService.CreateConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionService.UpdateConferenceEdition;

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


    @Override
    public ResponseEntity<ConferenceEditionResponse> createConferenceEdition(Instant applicationDeadlineTime, MultipartFile invitationList) {
        log.info("Got request from user to create conference edition with application deadline time: {} and invitation list: {}",
                applicationDeadlineTime, invitationList);

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
        return ConferenceEditionApi.super.getAllConferenceEditions();
    }

    @Override
    public ResponseEntity<ConferenceEditionSummaryResponse> getConferenceEditionSummary() {
        return ConferenceEditionApi.super.getConferenceEditionSummary();
    }
}
