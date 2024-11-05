package org.zpi.conferoapi.application;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.*;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.orcid.OrcidService;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.presentation.PresenterRepository;
import org.zpi.conferoapi.security.SecurityUtils;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.openapitools.model.ApplicationStatus.*;
import static org.openapitools.model.ErrorReason.NO_ACTIVE_CONFERENCE_EDITION;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ApplicationService {

    ConferenceEditionRepository conferenceEditionRepository;
    UserRepository userRepository;
    PresenterRepository presenterRepository;
    SessionRepository sessionRepository;
    PresentationRepository presentationRepository;
    OrcidService orcidService;
    SecurityUtils securityUtils;
    ApplicationMapper applicationMapper;
    ApplicationCommentRepository applicationCommentRepository;

    public ApplicationResponse getApplication(Long applicationId) {
        var session = sessionRepository.findById(applicationId)
                .orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));
        return applicationMapper.sessionToApplicationResponse(session);
    }

    public List<ApplicationPreviewResponse> getApplications() {
        return sessionRepository.findAll().stream().map(applicationMapper::toPreviewDto)
                .toList();
    }

    public void deleteApplication(Long applicationId) {
        User currentUser = securityUtils.getCurrentUser();
        var application = sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                currentUser.getId(),
                List.of(ApplicationStatus.DRAFT, PENDING)
        ).orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));
        sessionRepository.delete(application);
    }

    public ApplicationPreviewResponse reviewApplication(Long applicationId, ReviewRequest request) {
        var currentUser = securityUtils.getCurrentUser();
        if (!currentUser.getIsAdmin()) {
            throw new ServiceException(ErrorReason.UNAUTHORIZED);
        }
        var application = sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                currentUser.getId(),
                List.of(PENDING)
        ).orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));

        switch (request.getType()) {
            case ACCEPT -> application.setStatus(ACCEPTED);
            case REJECT -> application.setStatus(REJECTED);
            case ASK_FOR_ADJUSTMENTS -> {
                ApplicationComment comment = new ApplicationComment();
                comment.setSession(application);
                comment.setUser(currentUser);
                comment.setCreatedAt(Instant.now());
                comment.setContent(request.getComment());
                applicationCommentRepository.save(comment);
                application.setStatus(CHANGE_REQUESTED);
            }
        }
        sessionRepository.save(application);

        return applicationMapper.toPreviewDto(application);
    }

    public ApplicationPreviewResponse updateApplication(Long applicationId, UpdateApplicationRequest request) {
        var currentUser = securityUtils.getCurrentUser();
        var application = sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                currentUser.getId(),
                List.of(DRAFT, PENDING)
        ).orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));
        application = applicationMapper.partialUpdate(request, application);

        sessionRepository.save(application);

        return applicationMapper.toPreviewDto(application);
    }

    public Session createApplication(CreateApplicationRequest request) {
        var currentUser = securityUtils.getCurrentUser();
        var activeConferenceEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeConferenceEdition.isEmpty()) {
            throw new ServiceException(NO_ACTIVE_CONFERENCE_EDITION);
        }

        var session = Session.builder()
                .title(request.getTitle())
                .type(request.getType())
                .creator(currentUser)
                .tags(request.getTags())
                .edition(activeConferenceEdition.get())
                .description(request.getDescription())
                .status(request.getSaveAsDraft() ? ApplicationStatus.DRAFT : PENDING)
                .createdAt(Instant.now())
                .presentations(new ArrayList<>())
                .build();

        sessionRepository.save(session);

        request.getPresentations().forEach(presentationRequest -> {

            var newPresentation = Presentation.builder()
                    .title(presentationRequest.getTitle())
                    .session(session)
                    .presenters(new ArrayList<>())
                    .build();

            var savedPresentation = presentationRepository.save(newPresentation);


            presentationRequest.getPresenters().forEach(presenter -> {
                var existingUser = userRepository.findByEmailOrOrcid(presenter.getEmail(), presenter.getOrcid())
                        .orElseGet(() ->
                                userRepository.save(
                                        User.builder()
                                                .isAdmin(false)
                                                .email(presenter.getEmail())
                                                .build()
                                )
                        );

                OrcidInfoResponse presenterInfo;
                try {
                    presenterInfo = orcidService.getRecord(presenter.getOrcid());
                } catch (Exception e) {
                    throw new ServiceException(ErrorReason.INVALID_ORCID);
                }

                Presenter newPresenter = Presenter.builder()
                        .email(presenter.getEmail())
                        .presentation(savedPresentation)
                        .orcid(presenter.getOrcid())
                        .name(presenterInfo.getName())
                        .surname(presenterInfo.getSurname())
                        .title(presenterInfo.getTitle())
                        .organization(presenterInfo.getOrganization())
                        .isSpeaker(presenter.getIsSpeaker())
                        .user(existingUser)
                        .build();
                presenterRepository.save(newPresenter);
                savedPresentation.getPresenters().add(newPresenter);
            });
            session.getPresentations().add(savedPresentation);
        });
        return session;
    }

}
