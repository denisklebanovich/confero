package org.zpi.conferoapi.application;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.*;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.orcid.OrcidService;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.security.SecurityUtils;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.openapitools.model.ApplicationStatus.*;
import static org.openapitools.model.ErrorReason.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ApplicationService {

    ConferenceEditionRepository conferenceEditionRepository;
    UserRepository userRepository;
    SessionRepository sessionRepository;
    PresentationRepository presentationRepository;
    OrcidService orcidService;
    SecurityUtils securityUtils;
    ApplicationMapper applicationMapper;
    ApplicationCommentRepository applicationCommentRepository;

    public ApplicationResponse getApplication(Long applicationId) {
        Session application = getApplicationForUser(applicationId);
        boolean isFromActiveConference = isFromActiveConference(application);
        return applicationMapper.sessionToApplicationResponse(application)
                .fromActiveConferenceEdition(isFromActiveConference);
    }

    public List<ApplicationPreviewResponse> getApplications() {
        List<Session> applications = getApplicationsForCurrentUser();
        return applications.stream()
                .map(session -> applicationMapper.toPreviewDto(session)
                        .fromActiveConferenceEdition(isFromActiveConference(session)))
                .toList();
    }

    public void deleteApplication(Long applicationId) {
        Session session = findRemovableApplication(applicationId);
        sessionRepository.delete(session);
    }

    public ApplicationPreviewResponse reviewApplication(Long applicationId, ReviewRequest request) {
        Session session = findSessionWithStatus(applicationId, List.of(PENDING));
        updateReviewStatus(session, request);
        sessionRepository.save(session);
        return applicationMapper.toPreviewDto(session);
    }

    public ApplicationPreviewResponse updateApplication(Long applicationId, UpdateApplicationRequest request) {
        Session session = findEditableApplication(applicationId);
        log.info("Application before update: {}", session);
        updateSessionWithRequest(session, request);
        var updatedApplication = sessionRepository.save(session);
        log.info("Application after update: {}", updatedApplication);
        return applicationMapper.toPreviewDto(session);
    }

    public Session createApplication(CreateApplicationRequest request) {
        validateApplicationRequest(request);
        validateActiveConference();
        Session session = createNewSession(request);
        sessionRepository.save(session);
        addPresentationsToSession(session, request.getPresentations());
        return session;
    }

    private Session getApplicationForUser(Long applicationId) {
        return (securityUtils.isCurrentUserAdmin()
                ? sessionRepository.findById(applicationId)
                : sessionRepository.findByIdAndCreatorId(applicationId, securityUtils.getCurrentUser().getId()))
                .orElseThrow(() -> new ServiceException(APPLICATION_NOT_FOUND));
    }

    private boolean isFromActiveConference(Session session) {
        return conferenceEditionRepository.findActiveEditionConference()
                .map(activeEdition -> activeEdition.getId().equals(session.getEdition().getId()))
                .orElse(false);
    }

    private List<Session> getApplicationsForCurrentUser() {
        return securityUtils.isCurrentUserAdmin()
                ? sessionRepository.findAllByStatusNot(ACCEPTED)
                : sessionRepository.findAllByCreatorIdAndStatusNot(securityUtils.getCurrentUser().getId(), ACCEPTED);
    }

    private Session findRemovableApplication(Long applicationId) {
        return sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                securityUtils.getCurrentUser().getId(),
                List.of(DRAFT, PENDING)
        ).orElseThrow(() -> new ServiceException(APPLICATION_NOT_FOUND));
    }

    private Session findEditableApplication(Long applicationId) {
        return sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                securityUtils.getCurrentUser().getId(),
                List.of(DRAFT, PENDING, CHANGE_REQUESTED)
        ).orElseThrow(() -> new ServiceException(APPLICATION_NOT_FOUND));
    }

    private Session findSessionWithStatus(Long applicationId, List<ApplicationStatus> statuses) {
        return sessionRepository.findByIdAndStatusIsIn(applicationId, statuses)
                .orElseThrow(() -> new ServiceException(APPLICATION_NOT_FOUND));
    }

    private void updateReviewStatus(Session session, ReviewRequest request) {
        switch (request.getType()) {
            case ACCEPT -> session.setStatus(ACCEPTED);
            case REJECT -> session.setStatus(REJECTED);
            case ASK_FOR_ADJUSTMENTS -> addReviewComment(session, request.getComment());
        }
    }

    private void addReviewComment(Session session, String commentContent) {
        ApplicationComment comment = new ApplicationComment();
        comment.setSession(session);
        comment.setCreatedAt(Instant.now());
        comment.setContent(commentContent);
        applicationCommentRepository.save(comment);
        session.setStatus(CHANGE_REQUESTED);
    }

    private void updateSessionWithRequest(Session session, UpdateApplicationRequest request) {
        Optional.ofNullable(request.getTitle()).ifPresent(session::setTitle);
        Optional.ofNullable(request.getType()).ifPresent(session::setType);
        Optional.ofNullable(request.getTags()).ifPresent(session::setTags);
        Optional.ofNullable(request.getDescription()).ifPresent(session::setDescription);
        Optional.ofNullable(request.getPresentations()).ifPresent(presentations -> {
            if (!presentations.isEmpty()) {
                session.getPresentations().clear();
                addPresentationsToSession(session, presentations);
            }
        });

        if (Boolean.FALSE.equals(request.getSaveAsDraft()) || session.getStatus() == CHANGE_REQUESTED) {
            session.setStatus(PENDING);
        }
    }

    private void validateActiveConference() {
        if (conferenceEditionRepository.findActiveEditionConference().isEmpty()) {
            throw new ServiceException(NO_ACTIVE_CONFERENCE_EDITION);
        }
    }

    private Session createNewSession(CreateApplicationRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        return Session.builder()
                .title(request.getTitle())
                .type(request.getType())
                .creator(currentUser)
                .tags(request.getTags())
                .edition(conferenceEditionRepository.findActiveEditionConference().get())
                .description(request.getDescription())
                .status(request.getSaveAsDraft() ? DRAFT : PENDING)
                .createdAt(Instant.now())
                .presentations(new ArrayList<>())
                .build();
    }

    private void addPresentationsToSession(Session session, List<PresentationRequest> presentationRequests) {
        for (PresentationRequest presentationRequest : presentationRequests) {
            Presentation presentation = createPresentation(session, presentationRequest);
            session.getPresentations().add(presentation);
        }
    }

    private Presentation createPresentation(Session session, PresentationRequest presentationRequest) {
        Presentation presentation = Presentation.builder()
                .title(presentationRequest.getTitle())
                .session(session)
                .description(presentationRequest.getDescription())
                .presenters(new ArrayList<>())
                .build();

        Presentation savedPresentation = presentationRepository.save(presentation);
        addPresentersToPresentation(savedPresentation, presentationRequest.getPresenters());
        return savedPresentation;
    }

    private void addPresentersToPresentation(Presentation presentation, List<PresenterRequest> presenterRequests) {
        List<CompletableFuture<Presenter>> presenterFutures = presenterRequests.stream()
                .map(presenterRequest -> getOrcidInfoAsync(presenterRequest.getOrcid())
                        .thenApply(orcidInfo -> createPresenter(presentation, presenterRequest, orcidInfo)))
                .toList();

        List<Presenter> presenters = presenterFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        presentation.getPresenters().addAll(presenters);
    }

    private Presenter createPresenter(Presentation presentation, PresenterRequest presenterRequest, OrcidInfoResponse orcidInfo) {
        return Presenter.builder()
                .email(presenterRequest.getEmail())
                .presentation(presentation)
                .orcid(presenterRequest.getOrcid())
                .name(orcidInfo.getName())
                .surname(orcidInfo.getSurname())
                .title(orcidInfo.getTitle())
                .organization(orcidInfo.getOrganization())
                .isSpeaker(presenterRequest.getIsSpeaker())
                .build();
    }


    private CompletableFuture<OrcidInfoResponse> getOrcidInfoAsync(String orcid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return orcidService.getRecord(orcid);
            } catch (Exception e) {
                throw new ServiceException(INVALID_ORCID);
            }
        });
    }


    private void validateApplicationRequest(CreateApplicationRequest request) {
        if (request.getPresentations().isEmpty()) {
            throw new ServiceException(NO_PRESENTATIONS_PROVIDED);
        }
        if (request.getPresentations().stream().anyMatch(presentation -> presentation.getPresenters().isEmpty())) {
            throw new ServiceException(NO_PRESENTERS_PROVIDED);
        }
    }
}
