package org.zpi.conferoapi.session;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionPreviewResponse;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.security.SecurityUtils;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SessionService {

    SessionRepository sessionRepository;
    ConferenceEditionRepository conferenceEditionRepository;
    SessionMapper sessionMapper;
    SecurityUtils securityUtils;

    public List<SessionPreviewResponse> getSessions() {
        return getSessionsWithConfiguredTimeTable()
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromActiveConferenceEdition(isFromActiveConference(session))
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                )
                .peek(session -> {
                    log.info("Returning session: {}", session);
                })
                .toList();
    }

    public List<SessionPreviewResponse> getManagableSessions() {
        var user = securityUtils.getCurrentUser();
        var participations = sessionRepository.findUsersParticipations(user.getOrcid(), user.getEmailList());

        return participations.stream()
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromActiveConferenceEdition(isFromActiveConference(session))
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                )
                .peek(session -> {
                    log.info("Returning managable session: {}", session);
                })
                .toList();
    }

    private boolean isFromActiveConference(Session session) {
        return conferenceEditionRepository.findActiveEditionConference()
                .map(activeEdition -> activeEdition.getId().equals(session.getEdition().getId()))
                .orElse(false);
    }

    private Stream<Session> getSessionsWithConfiguredTimeTable() {
        return sessionRepository.findAllByStatus(ApplicationStatus.ACCEPTED)
                .stream().filter(Session::isTimeTableConfigured);
    }


    private boolean isSessionHasConfiguredTimeTable(Session session) {
        return session.getPresentations().stream()
                .allMatch(presentation -> presentation.getStartTime() != null && presentation.getEndTime() != null);
    }
}
