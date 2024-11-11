package org.zpi.conferoapi;


import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.model.ApplicationStatus;
import org.openapitools.model.SessionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.presentation.Presentation;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.Presenter;
import org.zpi.conferoapi.presentation.PresenterRepository;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.User;
import org.zpi.conferoapi.user.UserRepository;

import java.time.Instant;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                IntegrationTestConfiguration.class,
                ConferoApiApplication.class,
                TransactionTestConfiguration.class
        }
)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:17-alpine:///confero"
})
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .waitingFor(Wait.defaultWaitStrategy());

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ConferenceEditionRepository conferenceEditionRepository;

    @Autowired
    protected PresenterRepository presenterRepository;

    @Autowired
    protected SessionRepository sessionRepository;

    @Autowired
    protected PresentationRepository presentationRepository;

    @Autowired
    protected UserEmailRepository userEmailRepository;

    @Autowired
    protected TestTransactionalService tx;

    protected static final String ORCID = "0000-0002-5678-1234";

    protected static final String ORCID_2 = "0000-0002-5678-1235";

    protected static final String EMAIL = "example@gmail.com";
    protected static final String ADMIN_EMAIL = "admin@gmail.com";

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        tx.runInNewTransaction(() -> {
            presenterRepository.deleteAll();
            presentationRepository.deleteAll();
            sessionRepository.deleteAll();
            conferenceEditionRepository.deleteAll();
            userRepository.deleteAll();
            entityManager.flush();
            entityManager.clear();
            return null;
        });
    }



    protected void setAdminRights(User user) {
        tx.runInNewTransaction(() -> {
            user.setIsAdmin(true);
            userRepository.save(user);
            return null;
        });
    }

    protected User getUser() {
        return tx.runInNewTransaction(() -> {
            var savedUser = userRepository.save(User.builder()
                    .id(1L)
                    .isAdmin(false)
                    .build());
            userEmailRepository.save(new UserEmail(EMAIL, true, savedUser, null));
            return savedUser;
        });
    }


    protected User givenUser(String orcid, String accessToken, String avatarUrl, boolean isAdmin, List<String> emails) {
        return tx.runInNewTransaction(() -> {
            User user = User.builder()
                    .orcid(orcid)
                    .accessToken(accessToken)
                    .avatarUrl(avatarUrl)
                    .isAdmin(isAdmin)
                    .build();
            user.setEmails(emails.stream().map(email -> new UserEmail(email, true, user, null)).toList());
            return userRepository.save(user);
        });
    }

    protected ConferenceEdition givenConferenceEdition(Instant applicationDeadlineTime) {
        return tx.runInNewTransaction(() -> {
            ConferenceEdition conferenceEdition = ConferenceEdition.builder()
                    .applicationDeadlineTime(applicationDeadlineTime)
                    .createdAt(Instant.now())
                    .build();
            return conferenceEditionRepository.save(conferenceEdition);
        });
    }

    protected Session givenSession(String title, SessionType type, User creator, ConferenceEdition edition, String description) {
        return tx.runInNewTransaction(() -> {
            Session session = Session.builder()
                    .title(title)
                    .type(type)
                    .creator(creator)
                    .edition(edition)
                    .tags(List.of("Some", "default", "tags"))
                    .description(description)
                    .status(ApplicationStatus.ACCEPTED)
                    .createdAt(Instant.now())
                    .build();
            return sessionRepository.save(session);
        });
    }


    protected Presentation givenPresentation(String title, String description, Session session, Instant startTime, Instant endTime) {
        return tx.runInNewTransaction(() -> {
            Presentation presentation = Presentation.builder()
                    .title(title)
                    .description(description)
                    .session(session)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
            return presentationRepository.save(presentation);
        });
    }


    protected Presenter givenPresenter(String email, String orcid, String name, String surname, String title, String organization, Boolean isSpeaker, Presentation presentation) {
        return tx.runInNewTransaction(() -> {
            Presenter presenter = Presenter.builder()
                    .email(email)
                    .orcid(orcid)
                    .name(name)
                    .surname(surname)
                    .title(title)
                    .organization(organization)
                    .isSpeaker(isSpeaker)
                    .presentation(presentation)
                    .build();
            return presenterRepository.save(presenter);
        });
    }

    protected void givenAgendaForUser(User user, List<Session> sessions) {
        tx.runInNewTransaction(() -> {
            user.setAgenda(sessions);
            userRepository.save(user);
            return null;
        });
    }
}
