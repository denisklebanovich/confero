package org.zpi.conferoapi.conference;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

public interface ConferenceEditionService {

    ConferenceEdition createConferenceEdition(CreateConferenceEdition createConferenceEditionRequest);


    @Builder
    @Getter
    class CreateConferenceEdition {
        Instant applicationDeadlineTime;
        MultipartFile invitationList;
    }

}
