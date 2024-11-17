package org.zpi.conferoapi.session;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.configuration.S3Service;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.presentation.*;
import org.zpi.conferoapi.security.SecurityUtils;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.openapitools.model.ErrorReason.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SessionService {

    SessionRepository sessionRepository;
    ConferenceEditionRepository conferenceEditionRepository;
    SessionMapper sessionMapper;
    SecurityUtils securityUtils;
    UserRepository userRepository;
    PresentationRepository presentationRepository;
    PresenterRepository presenterRepository;
    S3Service s3Service;
    AttachmentRepository attachmentRepository;

    public List<SessionPreviewResponse> getSessions() {
        return getSessionsWithConfiguredTimeTable()
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromCurrentConferenceEdition(isFromCurrentConference(session))
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
        if (Objects.isNull(user.getOrcid()) && (Objects.isNull(user.getEmails()) || user.getEmails().isEmpty())) {
            System.out.println("Returning empty list");
            return List.of();
        }
        var participations = sessionRepository.findUsersParticipations(user.getOrcid(), user.getEmailList());

        return participations.stream()
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromCurrentConferenceEdition(isFromCurrentConference(session))
                        .startTime(getSessionStartTime(session).orElse(null))
                        .endTime(getSessionEndTime(session).orElse(null))
                        .isInCalendar(userHasSessionInAgenda(securityUtils.getCurrentUser(), session))
                )
                .peek(session -> {
                    log.info("Returning managable session: {}", session);
                })
                .toList();
    }


    public List<SessionPreviewResponse> getPersonalAgenda() {
        var user = securityUtils.getCurrentUser();
        return user.getAgenda().stream()
                .filter(this::isFromCurrentConference)
                .map(session -> sessionMapper.toPreviewDto(session)
                        .fromCurrentConferenceEdition(true)
                        .startTime(getSessionStartTime(session).orElse(null))
                        .endTime(getSessionEndTime(session).orElse(null))
                        .isInCalendar(true)
                )
                .peek(session -> {
                    log.info("Returning session from personal agenda: {}", session);
                })
                .toList();
    }

    public void addSessionToAgenda(AddSessionToAgendaRequest request) {
        var user = securityUtils.getCurrentUser();
        var session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));

        if (!isFromCurrentConference(session)) {
            throw new ServiceException(SESSION_IS_NOT_FROM_CURRENT_CONFERENCE_EDITION);
        }

        if (!userHasSessionInAgenda(user, session)) {
            user.getAgenda().add(session);
            userRepository.save(user);
        }
    }

    public void removeSessionFromAgenda(RemoveSessionFromAgendaRequest request) {
        var user = securityUtils.getCurrentUser();
        var session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));

        if (!isFromCurrentConference(session)) {
            throw new ServiceException(SESSION_IS_NOT_FROM_CURRENT_CONFERENCE_EDITION);
        }

        if (userHasSessionInAgenda(user, session)) {
            user.getAgenda().remove(session);
            userRepository.save(user);
        }
    }

    public SessionResponse getSession(Long sessionId) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));
        var user = securityUtils.getCurrentUser();
        var presentationsUserParticipatesIn = presentationRepository.findUserParticipations(user);

        var resp =  sessionMapper.toDto(session, sessionRepository.isUserParticipantForSession(sessionId, user.getOrcid(), user.getEmailList()))
                .fromCurrentConferenceEdition(isFromCurrentConference(session));

        var presentations = resp.getPresentations().stream()
                .map(p -> p.isMine(presentationsUserParticipatesIn.contains(p.getId())));

        return resp.presentations(presentations.toList());
    }


    public SessionResponse updateSession(Long sessionId, UpdateSessionRequest request) {
        var user = securityUtils.getCurrentUser();
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));

        if (!isFromCurrentConference(session)) {
            throw new ServiceException(SESSION_IS_NOT_FROM_CURRENT_CONFERENCE_EDITION);
        }

        request.getPresentations().forEach(presentation -> {
            var presentationToUpdate = session.getPresentations().stream()
                    .filter(p -> p.getId().equals(presentation.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(PRESENTATION_NOT_FOUND));

            findPresenterByUser(presentationToUpdate, user)
                    .orElseThrow(() -> new ServiceException(ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION));

            if (Objects.isNull(presentation.getStartTime()) ^ Objects.isNull(presentation.getEndTime())) {
                throw new ServiceException(BOTH_START_AND_END_TIME_MUST_BE_PROVIDED_FOR_PRESENTATION_UPDATE);
            }

            if (Objects.nonNull(presentation.getStartTime()) && Objects.nonNull(presentation.getEndTime()) && presentation.getStartTime().isAfter(presentation.getEndTime())) {
                throw new ServiceException(START_TIME_MUST_BE_BEFORE_END_TIME);
            }

            Optional.ofNullable(presentation.getTitle()).ifPresent(presentationToUpdate::setTitle);
            Optional.ofNullable(presentation.getDescription()).ifPresent(presentationToUpdate::setDescription);
            Optional.ofNullable(presentation.getStartTime()).ifPresent(presentationToUpdate::setStartTime);
            Optional.ofNullable(presentation.getEndTime()).ifPresent(presentationToUpdate::setEndTime);
            presentationRepository.save(presentationToUpdate);
        });

        var presentationsUserParticipatesIn = presentationRepository.findUserParticipations(user);

        var resp =  sessionMapper.toDto(session, true)
                .fromCurrentConferenceEdition(true);

        var presentations = resp.getPresentations().stream()
                .map(p -> p.isMine(presentationsUserParticipatesIn.contains(p.getId())));

        return resp.presentations(presentations.toList());
    }

    public int addAllSessionsByOrganizerToAgenda(Long presenterId) {
        var user = securityUtils.getCurrentUser();

        var presenter = presenterRepository.findById(presenterId)
                .orElseThrow(() -> new ServiceException(PRESENTER_NOT_FOUND));

        var presenterParticipations = sessionRepository.findParticipationsByOrganizerId(presenter.getId());

        var sessionsToAddToAgenda = presenterParticipations.stream()
                .filter(this::isFromCurrentConference)
                .filter(session -> !userHasSessionInAgenda(securityUtils.getCurrentUser(), session))
                .toList();

        user.getAgenda().addAll(sessionsToAddToAgenda);
        userRepository.save(user);

        return sessionsToAddToAgenda.size();
    }


    public AttachmentResponse addPresentationAttachment(Long sessionId, Long presentationId, MultipartFile file) {
        var user = securityUtils.getCurrentUser();

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));

        var presentation = session.getPresentations().stream()
                .filter(p -> p.getId().equals(presentationId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(PRESENTATION_NOT_FOUND));

        var presenter = findPresenterByUser(presentation, user)
                .orElseThrow(() -> new ServiceException(ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION));

        String key = "attachments/" + presentationId + "/" + file.getOriginalFilename();
        String url = s3Service.uploadFile(key, file);

        var attachment = Attachment.builder()
                .name(file.getOriginalFilename())
                .createdAt(Instant.now())
                .creator(presenter)
                .url(url)
                .build();

        attachmentRepository.save(attachment);

        return new AttachmentResponse()
                .id(attachment.getId())
                .name(attachment.getName())
                .url(attachment.getUrl());
    }


    public void deletePresentationAttachment(Long sessionId, Long attachmentId, Long presentationId) {
        var user = securityUtils.getCurrentUser();

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ServiceException(SESSION_NOT_FOUND));

        var presentation = session.getPresentations().stream()
                .filter(p -> p.getId().equals(presentationId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(PRESENTATION_NOT_FOUND));

        findPresenterByUser(presentation, user)
                .orElseThrow(() -> new ServiceException(ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION));

        var attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ServiceException(ATTACHMENT_NOT_FOUND));

        attachmentRepository.deleteById(attachment.getId());
    }

    public List<SesssionEventResponse> getSessionEvents(Integer pageSize) {
        var attachments = getSessionsWithConfiguredTimeTable()
                .filter(this::isFromCurrentConference)
                .flatMap(session -> session
                        .getPresentations()
                        .stream()
                        .flatMap(presentation -> presentation.getAttachments().stream()))
                .sorted(Comparator.comparing(Attachment::getCreatedAt).reversed())
                .limit(pageSize);

        return attachments.map(attachment -> new SesssionEventResponse()
                .id(attachment.getId())
                .type(SessionEventType.FILE_UPLOAD)
                .timestamp(attachment.getCreatedAt())
                .userFirstName(attachment.getCreator().getName())
                .userLastName(attachment.getCreator().getSurname())
                .sessionId(attachment.getCreator().getPresentation().getSession().getId())
        ).toList();
    }


    private boolean isFromCurrentConference(Session session) {
        return conferenceEditionRepository.findCurrentConferenceEdition()
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


    private Optional<Presenter> findPresenterByUser(Presentation presentation, User user) {
        return presentation.getPresenters().stream()
                .filter(presenter -> (
                        user.getEmailList().contains(presenter.getEmail())
                                ||
                                user.getOrcid().equals(presenter.getOrcid())
                ))
                .findFirst();
    }
}
