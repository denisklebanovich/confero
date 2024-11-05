package org.zpi.conferoapi.application;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.orcid.OrcidService;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ApplicationControllerTest extends IntegrationTestBase {

    private static final String ORCID = "0000-0002-5678-1234";

    private static final String EMAIL = "example@gmail.com";

    @MockBean
    OrcidService orcidService;

    @Test
    void createApplicationSuccess() {
        userRepository.save(User.builder()
                .email(EMAIL).isAdmin(false).build());

        conferenceEditionRepository.save(ConferenceEdition.builder()
                .id(1L)
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                .build());

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
    }

    @Test
    void createApplicationShouldFailCausedByNoActiveConferenceEdition() {
        userRepository.save(User.builder()
                .email(EMAIL).isAdmin(false).build());

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
        userRepository.save(User.builder()
                .email(EMAIL).isAdmin(false).build());

        conferenceEditionRepository.save(ConferenceEdition.builder()
                .id(1L)
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                .build());

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
        var user = userRepository.save(User.builder()
                .email(EMAIL).isAdmin(false).build());

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(new ReviewRequest().type(ReviewType.ACCEPT))
                .post("/api/application/1/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.UNAUTHORIZED.value());



        conferenceEditionRepository.save(ConferenceEdition.builder()
                .id(1L)
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                .build());

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


        user.setIsAdmin(true);
        userRepository.save(user);

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", EMAIL)
                .body(new ReviewRequest().type(ReviewType.ACCEPT))
                .post("/api/application/" + response.getId() + "/review")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());
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