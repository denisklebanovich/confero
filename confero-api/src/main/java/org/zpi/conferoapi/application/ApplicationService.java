package org.zpi.conferoapi.application;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.CreateApplicationRequest;
import org.springframework.stereotype.Service;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.exception.ServiceException;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.presentation.PresenterRepository;
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

    public Session createApplication(CreateApplicationRequest request, User user) {
        var activeConferenceEdition = conferenceEditionRepository.findActiveEditionConference();

        if (activeConferenceEdition.isEmpty()) {
            throw new ServiceException(NO_ACTIVE_CONFERENCE_EDITION);
        }

        var newSession = Session.builder()
                .title(request.getTitle())
                .type(request.getType())
                .creator(user)
                .tags(request.getTags())
                .edition(activeConferenceEdition.get())
                .description(request.getDescription())
                .status(request.getSaveAsDraft() ? ApplicationStatus.DRAFT : ApplicationStatus.PENDING)
                .createdAt(Instant.now())
                .presentations(new ArrayList<>())
                .build();

        var savedSession = sessionRepository.save(newSession);

        request.getPresentations().forEach(presentationRequest -> {

            var newPresentation = Presentation.builder()
                    .title(presentationRequest.getTitle())
                    .session(savedSession)
                    .presenters(new ArrayList<>())
                    .build();

            var savedPresentation = presentationRepository.save(newPresentation);


            presentationRequest.getPresenters().forEach(presenter -> {
                var existingUser = userRepository.findByEmail(presenter.getEmail()).orElseGet(() ->
                        userRepository.save(
                                User.builder()
                                        .isAdmin(false)
                                        .email(presenter.getEmail())
                                        .build()
                        )
                );

                Presenter newPresenter = Presenter.builder()
                        .email(presenter.getEmail())
                        .presentation(savedPresentation) // Set the associated presentation
                        .orcid(presenter.getOrcid())
                        .name("TODO")
                        .surname("TODO")
                        .isMain(presenter.getIsSpeaker())
                        .user(existingUser)
                        .build();
                presenterRepository.save(newPresenter);
            });
        });

        return sessionRepository.findById(savedSession.getId()).get();
    }

}
