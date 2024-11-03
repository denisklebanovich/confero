package org.zpi.conferoapi.conference;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CreateConferenceEditionRequest;
import org.openapitools.model.ErrorReason;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.exception.ServiceException;

import java.time.Instant;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ConferenceEditionServiceAdapter implements ConferenceEditionService {

    ConferenceEditionRepository conferenceEditionRepository;

    @Override
    public ConferenceEdition createConferenceEdition(CreateConferenceEditionRequest createConferenceEditionRequest) {
        var activeEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeEdition.isPresent()) {
            throw new ServiceException(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS);
        }

        return conferenceEditionRepository.save(ConferenceEdition.builder()
                .applicationDeadlineTime(createConferenceEditionRequest.getApplicationDeadlineTime())
                .createdAt(Instant.now())
                .build());
    }
}
