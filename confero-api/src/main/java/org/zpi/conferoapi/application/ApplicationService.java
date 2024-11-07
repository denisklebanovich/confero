package org.zpi.conferoapi.application;


import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.ApplicationPreviewResponse;
import org.openapitools.model.ApplicationResponse;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.OrcidInfoResponse;
import org.openapitools.model.ReviewRequest;
import org.openapitools.model.UpdateApplicationRequest;
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
import java.util.Optional;

import static org.openapitools.model.ApplicationStatus.ACCEPTED;
import static org.openapitools.model.ApplicationStatus.CHANGE_REQUESTED;
import static org.openapitools.model.ApplicationStatus.DRAFT;
import static org.openapitools.model.ApplicationStatus.PENDING;
import static org.openapitools.model.ApplicationStatus.REJECTED;
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
        var session = (securityUtils.isCurrentUserAdmin()
                ? sessionRepository.findById(applicationId)
                : sessionRepository.findByIdAndCreatorId(applicationId, securityUtils.getCurrentUser().getId()))
                .orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));

        var activeConference = conferenceEditionRepository.findActiveEditionConference();

        return applicationMapper.sessionToApplicationResponse(session)
                .fromActiveConferenceEdition(
                        activeConference
                                .map(activeEdition -> activeEdition.getId().equals(session.getEdition().getId()))
                                .orElse(false)
                );
    }

    public List<ApplicationPreviewResponse> getApplications() {
        var activeConferenceEdition = conferenceEditionRepository.findActiveEditionConference();

        var applications = securityUtils.isCurrentUserAdmin()
                ? sessionRepository.findAllByStatusNot(ACCEPTED)
                : sessionRepository.findAllByCreatorIdAndStatusNot(securityUtils.getCurrentUser().getId(), ACCEPTED);

        return applications.stream()
                .map(application -> applicationMapper.toPreviewDto(application).fromActiveConferenceEdition(
                        activeConferenceEdition
                                .map(activeEdition -> activeEdition.getId().equals(application.getEdition().getId()))
                                .orElse(false)
                ))
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
        var application = sessionRepository.findByIdAndStatusIsIn(
                applicationId,
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
        final var application = sessionRepository.findByIdAndCreatorIdAndStatusIsIn(
                applicationId,
                currentUser.getId(),
                List.of(DRAFT, PENDING, CHANGE_REQUESTED)
        ).orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));

        Optional.of(request.getTitle()).ifPresent(application::setTitle);
        Optional.of(request.getType()).ifPresent(application::setType);
        Optional.of(request.getTags()).ifPresent(application::setTags);
        Optional.of(request.getDescription()).ifPresent(application::setDescription);
        Optional.of(request.getPresentations()).ifPresent(presentations -> {
            presentations.forEach(presentationRequest -> {
                var newPresentation = Presentation.builder()
                        .title(presentationRequest.getTitle())
                        .session(application)
                        .presenters(new ArrayList<>())
                        .build();

                var savedPresentation = presentationRepository.save(newPresentation);
                presentationRequest.getPresenters().forEach(presenterRequest -> {
                    var existingUser = userRepository.findByEmailOrOrcid(presenterRequest.getEmail(), presenterRequest.getOrcid())
                            .orElseGet(() ->
                                    userRepository.save(
                                            User.builder()
                                                    .isAdmin(false)
                                                    .email(presenterRequest.getEmail())
                                                    .orcid(presenterRequest.getOrcid())
                                                    .build()
                                    )
                            );

                    OrcidInfoResponse presenterInfo = Try.of(() -> orcidService.getRecord(presenterRequest.getOrcid()))
                            .getOrElseThrow(() -> new ServiceException(ErrorReason.INVALID_ORCID));
                    Presenter newPresenter = Presenter.builder()
                            .email(presenterRequest.getEmail())
                            .presentation(savedPresentation)
                            .orcid(presenterRequest.getOrcid())
                            .name(presenterInfo.getName())
                            .surname(presenterInfo.getSurname())
                            .title(presenterInfo.getTitle())
                            .organization(presenterInfo.getOrganization())
                            .isSpeaker(presenterRequest.getIsSpeaker())
                            .user(existingUser)
                            .build();
                    presenterRepository.save(newPresenter);
                    savedPresentation.getPresenters().add(newPresenter);
                });
                application.getPresentations().add(savedPresentation);
            });
        });

        Optional.of(request.getSaveAsDraft()).ifPresent(saveAsDraft -> {
            if (!saveAsDraft) {
                application.setStatus(PENDING);
            }
        });

        if (application.getStatus() == CHANGE_REQUESTED) {
            application.setStatus(PENDING);
        }
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
                                                .orcid(presenter.getOrcid())
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
