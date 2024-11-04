package org.zpi.conferoapi.application;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ApplicationPreviewResponse;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.PresentationRequest;
import org.openapitools.model.PresenterRequest;
import org.openapitools.model.SessionType;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.conference.ConferenceEdition;
import org.zpi.conferoapi.user.User;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationControllerTest extends IntegrationTestBase {

    private static final String ORCID = "0000-0002-5678-1234";

    @Test
    void createApplicationSuccess() {
        userRepository.save(User.builder().orcid(ORCID).isAdmin(false).build());

        conferenceEditionRepository.save(ConferenceEdition.builder()
                .id(1L)
                .createdAt(Instant.now())
                .applicationDeadlineTime(Instant.now().plus(2, DAYS))
                .build());


        // Prepare mock data for CreateApplicationRequest
        var presentationRequest = new PresentationRequest()
                .title("Introduction to AI")
                .addPresentersItem(new PresenterRequest()
                        .email("presenter1@example.com")
                        .isSpeaker(true)
                        .orcid("0000-0001-2345-6789"));

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
                .header("Authorization", "Bearer token")
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
        assertEquals("0000-0001-2345-6789", presenterResponse.getOrcid());
    }
}