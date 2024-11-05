package org.zpi.conferoapi.session;

import org.junit.jupiter.api.Test;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionType;
import org.zpi.conferoapi.DataJpaTestBase;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.MatcherAssert.assertThat;

class SessionRepositoryTest extends DataJpaTestBase {


    @Test
    void check_cascade_delete() {
        var conferenceEdition = conferenceEditionRepository.save(conferenceEditionRepository.save(ConferenceEdition.builder()
                .id(1L)
                .applicationDeadlineTime(Instant.now().plus(1, DAYS))
                .createdAt(Instant.now())
                .build()));


        var user_1 = userRepository.save(User.builder()
                .id(1L)
                .orcid("0000-0000-0000-0000")
                .email("artsi@gmail.com")
                .isAdmin(false)
                .emailVerified(true)
                .build());

        var user_2 = userRepository.save(User.builder()
                .id(2L)
                .orcid("0000-0000-0000-0001")
                .email("asfas@gmail.com")
                .isAdmin(false)
                .emailVerified(true)
                .build());


        var session = sessionRepository.save(Session.builder()
                .id(1L)
                .title("Session")
                .type(SessionType.SESSION)
                .creator(user_1)
                .tags(List.of("tag1", "tag2"))
                .description("description")
                .status(ApplicationStatus.DRAFT)
                .createdAt(Instant.now())
                .presentations(List.of())
                .edition(conferenceEdition)
                .build());


        var presentation = presentationRepository.save(Presentation.builder()
                .id(1L)
                .title("Presentation")
                .session(session)
                .presenters(List.of())
                .build());


        var presenter_1 = Presenter.builder()
                .id(1L)
                .email(user_1.getEmail())
                .orcid(user_1.getOrcid())
                .name("Artur")
                .surname("Sierżant")
                .isMain(true)
                .user(user_1)
                .presentation(presentation)
                .title("mgr")
                .organization("PJAT")
                .build();

        var presenter_2 = Presenter.builder()
                .id(2L)
                .email(user_2.getEmail())
                .orcid(user_2.getOrcid())
                .name("Artur")
                .surname("Sierżant")
                .isMain(true)
                .user(user_2)
                .title("mgr")
                .presentation(presentation)
                .organization("PJAT")
                .build();

        presenterRepository.save(presenter_1);
        presenterRepository.save(presenter_2);

        presentation.getPresenters().add(presenter_1);
        presentation.getPresenters().add(presenter_2);

        presentationRepository.save(presentation);

        session.getPresentations().add(presentation);
        sessionRepository.save(session);


        sessionRepository.delete(session);


        var sessionOptional = sessionRepository.findById(session.getId());
        var presentationOptional = presentationRepository.findById(presentation.getId());
        var presenter_1Optional = presenterRepository.findById(presenter_1.getId());
        var presenter_2Optional = presenterRepository.findById(presenter_2.getId());

        assertThat("Session should be deleted", sessionOptional.isEmpty());
        assertThat("Presentation should be deleted", presentationOptional.isEmpty());
        assertThat("Presenter 1 should be deleted", presenter_1Optional.isEmpty());
        assertThat("Presenter 2 should be deleted", presenter_2Optional.isEmpty());
    }

}