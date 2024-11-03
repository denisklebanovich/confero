package org.zpi.conferoapi.conference;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

public interface ConferenceEditionService {

    ConferenceEdition createConferenceEdition(CreateConferenceEdition createConferenceEditionRequest);


    @Value
    @Builder
    class CreateConferenceEdition {
        Instant applicationDeadlineTime;
        MultipartFile invitationList;
    }

}
