package org.zpi.conferoapi.session;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionPreviewResponse;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.security.SecurityUtils;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                        .startTime(getSessionStartTime(session).orElse(null))
                        .endTime(getSessionEndTime(session).orElse(null))
                        .isInCalendar(Try.of(() -> userHasSessionInAgenda(securityUtils.getCurrentUser(), session)).getOrElse(false))
                )
                .peek(session -> {
                    log.info("Returning session: {}", session);
                })
                .toList();
    }

    public List<SessionPreviewResponse> getManagableSessions() {
        var user = securityUtils.getCurrentUser();

        log.info("Getting managable sessions for user: {}", user);
        if(Objects.isNull(user.getOrcid()) && (Objects.isNull(user.getEmails()) || user.getEmails().isEmpty())) {
            System.out.println("Returning empty list");
            return List.of();
        }
        var participations = sessionRepository.findUsersParticipations(user.getOrcid(), user.getEmailList());

        return participations.stream()
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromActiveConferenceEdition(isFromActiveConference(session))
                        .startTime(getSessionStartTime(session).orElse(null))
                        .endTime(getSessionEndTime(session).orElse(null))
                        .isInCalendar(userHasSessionInAgenda(securityUtils.getCurrentUser(), session))
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
                .stream().filter(this::isSessionHasConfiguredTimeTable);
    }

    private boolean isSessionHasConfiguredTimeTable(Session session) {
        return session.getPresentations().stream()
                .allMatch(presentation -> presentation.startTime().isPresent() && presentation.endTime().isPresent());
    }

    private Optional<Instant> getSessionStartTime(Session session) {
        if (!isSessionHasConfiguredTimeTable(session)) {
            return Optional.empty();
        }

        return session.getPresentations().stream()
                .map(Presentation::startTime)
                .map(Optional::get)
                .min(Instant::compareTo);
    }


    private Optional<Instant> getSessionEndTime(Session session) {
        if (!isSessionHasConfiguredTimeTable(session)) {
            return Optional.empty();
        }

        return session.getPresentations().stream()
                .map(Presentation::endTime)
                .map(Optional::get)
                .max(Instant::compareTo);
    }


    private boolean userHasSessionInAgenda(User user, Session session) {
        return user.getAgenda().stream().anyMatch(s -> s.getId().equals(session.getId()));
    }
}
