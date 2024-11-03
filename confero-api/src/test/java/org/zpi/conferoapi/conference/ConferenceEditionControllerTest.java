package org.zpi.conferoapi.conference;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.CreateConferenceEditionRequest;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ErrorResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.zpi.conferoapi.ConferoApiApplication;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.IntegrationTestConfiguration;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfiguration.class, ConferoApiApplication.class}
)
class ConferenceEditionControllerTest extends IntegrationTestBase {

    @Test
    void createConferenceEdition() {

        var conferenceEdition = new CreateConferenceEditionRequest()
                .applicationDeadlineTime(Instant.now().plus(1, DAYS));

        var response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("applicationDeadlineTime", conferenceEdition.getApplicationDeadlineTime().toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        var createdConferenceEdition = response.as(ConferenceEditionResponse.class);
        assertNotNull(createdConferenceEdition.getId());
        assertEquals(conferenceEdition.getApplicationDeadlineTime(), createdConferenceEdition.getApplicationDeadlineTime());


        var conferenceEdition_2 = new CreateConferenceEditionRequest()
                .applicationDeadlineTime(Instant.now().plus(2, DAYS));


        var errorResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("applicationDeadlineTime", conferenceEdition_2.getApplicationDeadlineTime().toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .response();

        var error = errorResponse.as(ErrorResponse.class);
        assertEquals(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS, error.getReason());
    }
}