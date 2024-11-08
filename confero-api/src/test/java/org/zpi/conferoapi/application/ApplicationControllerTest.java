package org.zpi.conferoapi.application;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.orcid.OrcidService;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ApplicationControllerTest extends IntegrationTestBase {


    @MockBean
    OrcidService orcidService;

    @Test
    void createUpdateApplication() {

        tx.runInNewTransaction(() -> {
            var savedUser = userRepository.save(User.builder().isAdmin(false).build());
            userEmailRepository.save(new UserEmail(EMAIL, true, savedUser, null));

            conferenceEditionRepository.save(ConferenceEdition.builder()
                    .id(1L)
                    .createdAt(Instant.now())
                    .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                    .build());
            return null;
        });

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));


        // Prepare mock data for CreateApplicationRequest
        var presentationRequest = new PresentationRequest()
                .title("Introduction to AI")
                .addPresentersItem(new PresenterRequest()
                        .email("presenter1@example.com")
                        .isSpeaker(true)
                        .orcid(ORCID));

        var createApplicationRequest = new CreateApplicationRequest()
                .title("AI in Modern Science")
                .type(SessionType.WORKSHOP)
                .tags(List.of("AI", "Technology"))
                .description("A deep dive into AI's impact on science")
                .presentations(List.of(presentationRequest))
                .saveAsDraft(false);

        // Execute the POST request to create an application
        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest)
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        // Deserialize response into ApplicationPreviewResponse
        var createdApplication = response.as(ApplicationPreviewResponse.class);

        // Verify the response fields
        assertNotNull(createdApplication.getId());
        assertEquals(createApplicationRequest.getTitle(), createdApplication.getTitle());
        assertEquals(createApplicationRequest.getType(), createdApplication.getType());
        assertEquals("PENDING", createdApplication.getStatus().name()); // assuming default status is PENDING when not a draft
        assertEquals(1, createdApplication.getPresenters().size());

        var presenterResponse = createdApplication.getPresenters().get(0);
        assertTrue(presenterResponse.getIsSpeaker());
        assertEquals(ORCID, presenterResponse.getOrcid());
        assertEquals("John", presenterResponse.getName());
        assertEquals("Doe", presenterResponse.getSurname());

        var updateApplicationRequest = new UpdateApplicationRequest()
                .description("updated description");

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(updateApplicationRequest)
                .put("/api/application/" + createdApplication.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();


        tx.runInNewTransaction(() -> {
            sessionRepository.findById(createdApplication.getId()).ifPresent(session -> {
                assertEquals(updateApplicationRequest.getDescription(), session.getDescription());
            });
            return null;
        });

        var updatePresentationRequest = new UpdateApplicationRequest()
                .presentations(List.of(new PresentationRequest()
                        .title("Updated title")
                        .addPresentersItem(new PresenterRequest()
                                .email("arm@gmail.com")
                                .isSpeaker(true)
                                .orcid(ORCID)
                        )));

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(updatePresentationRequest)
                .put("/api/application/" + createdApplication.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();


        tx.runInNewTransaction(() -> {
            sessionRepository.findById(createdApplication.getId()).ifPresent(session -> {
                assertEquals(updatePresentationRequest.getPresentations().get(0).getTitle(), session.getPresentations().get(0).getTitle());
                assertEquals(updatePresentationRequest.getPresentations().get(0).getPresenters().get(0).getOrcid(), session.getPresentations().get(0).getPresenters().get(0).getOrcid());
            });
            return null;
        });
    }

    @Test
    void createApplicationShouldFailCausedByNoActiveConferenceEdition() {
        tx.runInNewTransaction(() -> {
            var savedUser = userRepository.save(User.builder().isAdmin(false).build());
            userEmailRepository.save(new UserEmail(EMAIL, true, savedUser, null));
            return null;
        });

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));

        // Execute the POST request to create an application
        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest())
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .response();

        // Deserialize response into ErrorResponse
        var errorResponse = response.as(ErrorResponse.class);
        assertEquals(errorResponse.getReason(), ErrorReason.NO_ACTIVE_CONFERENCE_EDITION);
    }

    @Test
    void shouldGetApplication() {
        tx.runInNewTransaction(() -> {
            var savedUser = userRepository.save(User.builder().isAdmin(false).build());
            userEmailRepository.save(new UserEmail(EMAIL, true, savedUser, null));
            conferenceEditionRepository.save(ConferenceEdition.builder()
                    .id(1L)
                    .createdAt(Instant.now())
                    .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                    .build());

            return null;
        });

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest())
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();
    }

    @Test
    void only_admin_can_review_application() {
        System.out.println("Running only_admin_can_review_application test");
        var user = getUser();

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(new ReviewRequest().type(ReviewType.ACCEPT))
                .post("/api/application/1/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.UNAUTHORIZED.value());


        tx.runInNewTransaction(() -> {
            conferenceEditionRepository.save(ConferenceEdition.builder()
                    .id(1L)
                    .createdAt(Instant.now())
                    .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                    .build());
            return null;
        });


        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest())
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response()
                .as(ApplicationPreviewResponse.class);


        setAdminRights(user);

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", ADMIN_EMAIL)
                .body(new ReviewRequest().type(ReviewType.ACCEPT))
                .post("/api/application/" + response.getId() + "/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void application_status_should_change_after_review() {
        System.out.println("Running application_status_should_change_after_review test");
        var user = getUser();
        createConferenceEdition();

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest())
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response()
                .as(ApplicationPreviewResponse.class);
        setAdminRights(user);


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(new ReviewRequest().type(ReviewType.ACCEPT))
                .post("/api/application/" + response.getId() + "/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .get("/api/application/" + response.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(ApplicationResponse.class);

        tx.runInNewTransaction(() -> {
            var session = sessionRepository.findById(response.getId()).orElseThrow();
            assertEquals(ApplicationStatus.ACCEPTED, session.getStatus());
            return null;
        });
    }

    private void createConferenceEdition() {
        tx.runInNewTransaction(() -> {
            conferenceEditionRepository.save(ConferenceEdition.builder()
                    .id(1L)
                    .createdAt(Instant.now())
                    .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                    .build());
            return null;
        });
    }


    @Test
    void admin_can_leave_comment_on_application() {
        System.out.println("Running admin_can_leave_comment_on_application test");
        var user = getUser();

        createConferenceEdition();

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest())
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response()
                .as(ApplicationPreviewResponse.class);

        setAdminRights(user);

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(new ReviewRequest().type(ReviewType.ASK_FOR_ADJUSTMENTS).comment("Good job!"))
                .post("/api/application/" + response.getId() + "/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .get("/api/application/" + response.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(ApplicationResponse.class);

        tx.runInNewTransaction(() -> {
            var session = sessionRepository.findById(response.getId()).orElseThrow();
            assertEquals(1, session.getComments().size());
            return null;
        });
    }


    @Test
    void presentation_can_have_multiple_main_speakers() {
        tx.runInNewTransaction(() -> {
            var savedUser = userRepository.save(User.builder().isAdmin(false).build());
            userEmailRepository.save(new UserEmail(EMAIL, true, savedUser, null));
            conferenceEditionRepository.save(ConferenceEdition.builder()
                    .id(1L)
                    .createdAt(Instant.now())
                    .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                    .build());
            return null;
        });

        when(orcidService.getRecord(ORCID)).thenReturn(new OrcidInfoResponse().name("John").surname("Doe"));
        when(orcidService.getRecord(ORCID_2)).thenReturn(new OrcidInfoResponse().name("Jane").surname("Doe"));


        var presentationRequest = presentationRequest()
                .addPresentersItem(presenterRequest().email("another@mail.com").orcid(ORCID_2));


        var createApplicationRequest = createApplicationRequest()
                .presentations(List.of(presentationRequest));

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(createApplicationRequest)
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response()
                .as(ApplicationPreviewResponse.class);


        response.getPresenters().forEach(presenter -> {
            assertTrue(presenter.getIsSpeaker());
        });
    }


    private PresenterRequest presenterRequest() {
        return new PresenterRequest()
                .email(EMAIL)
                .isSpeaker(true)
                .orcid(ORCID);
    }

    private PresentationRequest presentationRequest() {
        return new PresentationRequest()
                .title("Introduction to AI")
                .addPresentersItem(presenterRequest());
    }

    private CreateApplicationRequest createApplicationRequest() {
        return new CreateApplicationRequest()
                .title("AI in Modern Science")
                .type(SessionType.WORKSHOP)
                .tags(List.of("AI", "Technology"))
                .description("A deep dive into AI's impact on science")
                .presentations(List.of(presentationRequest()))
                .saveAsDraft(false);
    }
}