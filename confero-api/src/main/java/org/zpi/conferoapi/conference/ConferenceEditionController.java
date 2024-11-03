package org.zpi.conferoapi.conference;


import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.ConferenceEditionApi;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ConferenceEditionSummaryResponse;
import org.openapitools.model.CreateConferenceEditionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ConferenceEditionController implements ConferenceEditionApi {

    ConferenceEditionService conferenceEditionService;

    @Override
    public ResponseEntity<ConferenceEditionResponse> createConferenceEdition(CreateConferenceEditionRequest createConferenceEditionRequest) {
        log.info("Got request from user to create conference edition: {}", createConferenceEditionRequest);

        var created = conferenceEditionService.createConferenceEdition(createConferenceEditionRequest);

        log.info("Created conference edition: {}", created);
        return new ResponseEntity<>(
                new ConferenceEditionResponse()
                        .id(created.getId())
                        .applicationDeadlineTime(created.getApplicationDeadlineTime())
                        .numberOfInvitations(0)
                        .createdAt(created.getCreatedAt()),
                CREATED);
    }

    @Override
    public ResponseEntity<List<ConferenceEditionResponse>> getAllConferenceEditions() {
        return ConferenceEditionApi.super.getAllConferenceEditions();
    }

    @Override
    public ResponseEntity<ConferenceEditionSummaryResponse> getConferenceEditionSummary() {
        return ConferenceEditionApi.super.getConferenceEditionSummary();
    }

    @Override
    public ResponseEntity<ConferenceEditionResponse> inviteUsers(String conferenceEditionId, MultipartFile file) {
        return ConferenceEditionApi.super.inviteUsers(conferenceEditionId, file);
    }

    @Override
    public ResponseEntity<ConferenceEditionResponse> updateConferenceEdition(String conferenceEditionId, CreateConferenceEditionRequest createConferenceEditionRequest) {
        return ConferenceEditionApi.super.updateConferenceEdition(conferenceEditionId, createConferenceEditionRequest);
    }
}
