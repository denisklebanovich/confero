package org.zpi.conferoapi;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.email.UserEmailRepository;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.PresenterRepository;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:17-alpine:///confero"
})
public abstract class DataJpaTestBase {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .waitingFor(Wait.defaultWaitStrategy());

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ConferenceEditionRepository conferenceEditionRepository;

    @Autowired
    protected SessionRepository sessionRepository;

    @Autowired
    protected PresentationRepository presentationRepository;

    @Autowired
    protected PresenterRepository presenterRepository;

    @Autowired
    protected UserEmailRepository userEmailRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        conferenceEditionRepository.deleteAll();
        sessionRepository.deleteAll();
        presentationRepository.deleteAll();
        presenterRepository.deleteAll();
    }
}
