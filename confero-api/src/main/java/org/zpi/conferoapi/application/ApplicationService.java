package org.zpi.conferoapi.application;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.CreateApplicationRequest;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import static org.openapitools.model.ErrorReason.NO_ACTIVE_CONFERENCE_EDITION;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ApplicationService {

    ApplicationRepository applicationRepository;

    ConferenceEditionRepository conferenceEditionRepository;

    UserRepository userRepository;

    public Session createApplication(CreateApplicationRequest request, User user) {
        var activeConferenceEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeConferenceEdition.isEmpty()) {
            throw new ServiceException(NO_ACTIVE_CONFERENCE_EDITION);
        }

        return null;
    }

}
