package org.zpi.conferoapi;


import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.presentation.PresentationRepository;
import org.zpi.conferoapi.presentation.PresenterRepository;
import org.zpi.conferoapi.session.SessionRepository;
import org.zpi.conferoapi.user.UserRepository;

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
    protected TestTransactionalService tx;

    protected static final String ORCID = "0000-0002-5678-1234";

    protected static final String ORCID_2 = "0000-0002-5678-1235";

    protected static final String EMAIL = "example@gmail.com";

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
}
