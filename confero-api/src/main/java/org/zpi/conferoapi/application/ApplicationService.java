package org.zpi.conferoapi.application;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.ApplicationResponse;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.ErrorReason;
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

    public ApplicationResponse getApplication(Long applicationId) {
        var session = sessionRepository.findById(applicationId)
                .orElseThrow(() -> new ServiceException(ErrorReason.APPLICATION_NOT_FOUND));
        return applicationMapper.sessionToApplicationResponse(session);
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
                .status(request.getSaveAsDraft() ? ApplicationStatus.DRAFT : ApplicationStatus.PENDING)
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

                var presenterInfo = orcidService.getRecord(presenter.getOrcid());

                Presenter newPresenter = Presenter.builder()
                        .email(presenter.getEmail())
                        .presentation(savedPresentation)
                        .orcid(presenter.getOrcid())
                        .name(presenterInfo.getName())
                        .surname(presenterInfo.getSurname())
                        .title(presenterInfo.getTitle())
                        .organization(presenterInfo.getOrganization())
                        .isMain(presenter.getIsSpeaker())
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
