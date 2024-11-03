package org.zpi.conferoapi.conference;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;

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


    @Test
    void invalid_file_format() throws IOException {
        createConferenceEditionWithInvalidFileFormat("This, is, not a valid CSV file");
        createConferenceEditionWithInvalidFileFormat("This is not a valid CSV file");
    }

    void createConferenceEditionWithInvalidFileFormat(String content) throws IOException {
        // Prepare an invalid file (non-CSV format)
        File invalidFile = File.createTempFile("invalidInvitationList", ".txt");
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(content);
        }

        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var errorResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("invitationList", invalidFile)
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.BAD_REQUEST.value())  // Assuming invalid format results in a 400 error
                .extract()
                .response();

        var error = errorResponse.as(ErrorResponse.class);
        assertEquals(ErrorReason.INVALID_FILE_FORMAT, error.getReason());
    }
}