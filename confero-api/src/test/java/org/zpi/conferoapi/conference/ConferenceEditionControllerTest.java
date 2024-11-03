package org.zpi.conferoapi.conference;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ErrorResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zpi.conferoapi.ConferoApiApplication;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.IntegrationTestConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ConferenceEditionControllerTest extends IntegrationTestBase {


    @Test
    void createConferenceEditionWithoutInviteesListProvided() {
        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        var createdConferenceEdition = response.as(ConferenceEditionResponse.class);
        assertNotNull(createdConferenceEdition.getId());
        assertEquals(applicationDeadlineTime, createdConferenceEdition.getApplicationDeadlineTime());
        assertEquals(0, createdConferenceEdition.getNumberOfInvitations());

        var errorResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .response();

        var error = errorResponse.as(ErrorResponse.class);
        assertEquals(ErrorReason.ACTIVE_CONFERENCE_EDITION_ALREADY_EXISTS, error.getReason());
    }


    @Test
    void createConferenceEditionWithInviteesListProvided() throws IOException {
        String csvContent = "artsi@example.com\ndenis@example.com";

        // Write the CSV content to a temporary file
        File tempFile = File.createTempFile("invitationList", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(csvContent);
        }


        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("invitationList", tempFile) // Attach the CSV file here
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        var createdConferenceEdition = response.as(ConferenceEditionResponse.class);
        assertNotNull(createdConferenceEdition.getId());
        assertEquals(applicationDeadlineTime, createdConferenceEdition.getApplicationDeadlineTime());
        assertEquals(2, createdConferenceEdition.getNumberOfInvitations());
    }
}