package org.zpi.conferoapi;


import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zpi.conferoapi.application.ApplicationService;
import org.zpi.conferoapi.conference.ConferenceEditionRepository;
import org.zpi.conferoapi.presentation.PresenterRepository;
import org.zpi.conferoapi.user.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfiguration.class, ConferoApiApplication.class}
)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:17-alpine:///confero"
})
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class IntegrationTestBase {

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ConferenceEditionRepository conferenceEditionRepository;

    @Autowired
    protected PresenterRepository presenterRepository;


    protected static final String ORCID = "0000-0002-5678-1234";

    protected static final String ORCID_2 = "0000-0002-5678-1235";

    protected static final String EMAIL = "example@gmail.com";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userRepository.deleteAll();
        conferenceEditionRepository.deleteAll();
        presenterRepository.deleteAll();
    }
}
