package org.zpi.conferoapi.conference;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openapitools.model.ConferenceEditionResponse;
import org.openapitools.model.ConferenceEditionSummaryResponse;
import org.openapitools.model.ErrorReason;
import org.openapitools.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.email.UserEmail;
import org.zpi.conferoapi.user.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ConferenceEditionControllerTest extends IntegrationTestBase {

    @Test
    void createConferenceEditionWithoutInviteesListProvided() {
        var applicationDeadlineTime = Instant.now().plus(1, DAYS);
        var user = getUser();
        setAdminRights(user);

        var response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .header("Authorization", EMAIL)
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
                .header("Authorization", EMAIL)
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
        var user = getUser();
        setAdminRights(user);

        // Write the CSV content to a temporary file
        File tempFile = File.createTempFile("invitationList", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(csvContent);
        }


        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var response = RestAssured
                .given()
                .contentType("multipart/form-data")
                .header("Authorization", EMAIL)
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
        var user = getUser();
        setAdminRights(user);
        // Prepare an invalid file (non-CSV format)
        File invalidFile = File.createTempFile("invalidInvitationList", ".txt");
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(content);
        }

        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var errorResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .header("Authorization", EMAIL)
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


    @Test
    void updateConferenceEditionOverridingInvitationList() throws IOException {
        var user = getUser();
        setAdminRights(user);
        String initialCsvContent = "initialuser1@example.com\ninitialuser2@example.com";
        File initialFile = File.createTempFile("initialInvitationList", ".csv");
        try (FileWriter writer = new FileWriter(initialFile)) {
            writer.write(initialCsvContent);
        }

        var applicationDeadlineTime = Instant.now().plus(1, DAYS);

        var createResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .header("Authorization", EMAIL)
                .multiPart("invitationList", initialFile)
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .post("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        var createdConferenceEdition = createResponse.as(ConferenceEditionResponse.class);
        assertNotNull(createdConferenceEdition.getId());
        assertEquals(2, createdConferenceEdition.getNumberOfInvitations());

        String newCsvContent = "newuser1@example.com\nnewuser2@example.com";
        File newFile = File.createTempFile("newInvitationList", ".csv");
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(newCsvContent);
        }

        var updateResponse = RestAssured
                .given()
                .contentType("multipart/form-data")
                .header("Authorization", EMAIL)
                .multiPart("id", createdConferenceEdition.getId().toString())
                .multiPart("invitationList", newFile)  // Attach the new CSV file here
                .multiPart("applicationDeadlineTime", applicationDeadlineTime.toString())
                .put("/api/conference-edition/" + createdConferenceEdition.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        var updatedConferenceEdition = updateResponse.as(ConferenceEditionResponse.class);
        assertEquals(createdConferenceEdition.getId(), updatedConferenceEdition.getId());
        assertEquals(2, updatedConferenceEdition.getNumberOfInvitations());
    }


    @Test
    void getAllConferenceEditions() {
        var user = getUser();
        setAdminRights(user);

        tx.runInNewTransaction(() -> {
            conferenceEditionRepository.saveAndFlush(ConferenceEdition.builder()
                    .applicationDeadlineTime(Instant.now()).createdAt(Instant.now().plus(10, DAYS))
                    .build());

            conferenceEditionRepository.saveAndFlush(ConferenceEdition.builder()
                    .applicationDeadlineTime(Instant.now()).createdAt(Instant.now().plus(11, DAYS))
                    .build());

            return null;
        });

        var response = RestAssured
                .given()
                .header("Authorization", EMAIL)
                .get("/api/conference-edition")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        var conferenceEditions = response.as(ConferenceEditionResponse[].class);
        assertEquals(2, conferenceEditions.length);
        System.out.println("debug");
    }


    @Test
    void getConferenceEditionSummary_WhenActiveEditionExists() {
        tx.runInNewTransaction(() -> {
            // Create an active conference edition
            ConferenceEdition activeEdition = ConferenceEdition.builder()
                    .applicationDeadlineTime(Instant.now().plus(1, DAYS))
                    .createdAt(Instant.now())
                    .build();
            conferenceEditionRepository.save(activeEdition);
            return null;
        });

        var response = RestAssured
                .given()
                .get("/api/conference-edition/summary")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        var summary = response.as(ConferenceEditionSummaryResponse.class);
        assertNotNull(summary);
        assertEquals(true, summary.getAcceptingNewApplications());
    }

    @Test
    void getConferenceEditionSummary_WhenNoActiveEditionExists() {
        // No active conference editions should exist at this point
        var response = RestAssured
                .given()
                .get("/api/conference-edition/summary")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        var summary = response.as(ConferenceEditionSummaryResponse.class);
        assertNotNull(summary);
        assertEquals(false, summary.getAcceptingNewApplications());
    }
}