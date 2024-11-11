package org.zpi.conferoapi.session;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.SessionPreviewResponse;
import org.openapitools.model.SessionType;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SessionControllerTest extends IntegrationTestBase {

    @Test
    void getSessions() {
        var user = givenUser("0000-0002-5678-1234", "access-token", "http://example.com/avatar.png", false, List.of("artsi@gmail.com"));
        var conferenceEdition = givenConferenceEdition(Instant.now().plusSeconds(3600));
        var session = givenSession(
                "Test Session",
                SessionType.SESSION,
                user,
                conferenceEdition,
                "This is a test session description."
        );

        var presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                session,
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200)
        );

        givenPresenter(
                "presenter1@mail.ru",
                "orcid1",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        givenPresenter(
                "presenter2@mail.ru",
                "orcid2",
                "name2",
                "surname2",
                "title 2",
                "Politechnika Wrocławska",
                false,
                presentation
        );


        var response = RestAssured
                .given()
                .contentType("application/json")
                .get("/api/session")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);


        assertNotNull(response);
        assertEquals(1, response.length, "Expected one session in the response");

        var sessionResponse = response[0];

        assertEquals(session.getId(), sessionResponse.getId(), "Session ID mismatch");
        assertEquals(session.getTitle(), sessionResponse.getTitle(), "Session title mismatch");
        assertEquals(session.getTags(), sessionResponse.getTags(), "Session tags mismatch");
        assertNotNull(sessionResponse.getStartTime(), "Start time missing");
        assertNotNull(sessionResponse.getEndTime(), "End time mismatch");
        assertNotNull(sessionResponse.getFromActiveConferenceEdition(), "Active conference edition flag missing");
        assertEquals(true, sessionResponse.getFromActiveConferenceEdition(), "Session is not from active conference edition");

        var presenters = sessionResponse.getPresenters();
        assertNotNull(presenters, "Presenters should not be null");
        assertEquals(2, presenters.size(), "Expected two presenters in the response");

        var presenter1 = presenters.get(0);
        var presenter2 = presenters.get(1);

        assertEquals("name1", presenter1.getName(), "Presenter 1 name mismatch");
        assertEquals("surname1", presenter1.getSurname(), "Presenter 1 surname mismatch");

        assertEquals("name2", presenter2.getName(), "Presenter 2 name mismatch");
        assertEquals("surname2", presenter2.getSurname(), "Presenter 2 surname mismatch");
    }

    @Test
    void user_without_login_can_access_sessions() {
        RestAssured
                .given()
                .contentType("application/json")
                .get("/api/session")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());
    }



    @Test
    void getManagableSessionsByOrcidAndMail() {
        var user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );
        var conferenceEdition = givenConferenceEdition(Instant.now().plusSeconds(3600));
        var session = givenSession(
                "Test Session",
                SessionType.SESSION,
                user,
                conferenceEdition,
                "This is a test session description."
        );

        var presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                session,
                null,
                null
        );

        givenPresenter(
                "artsi@gmail.com",
                "orcid1",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );



        var session_2 = givenSession(
                "Test Session 2",
                SessionType.SESSION,
                user,
                conferenceEdition,
                "This is a test session description."
        );

        var presentation_2_start = Instant.now().plusSeconds(3600);
        var presentation_2_end = Instant.now().plusSeconds(7200);


        var presentation_2 = givenPresentation(
                "Test Presentation 2",
                "Presentation Description",
                session_2,
                presentation_2_start,
                presentation_2_end
        );


        givenPresenter(
                "random@gmail.com",
                "0000-0002-5678-1234",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation_2
        );


        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/manage")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(response);
        assertEquals(2, response.length, "Expected one session in the response");

        var sessionResponse = response[0];
        assertEquals(null, sessionResponse.getStartTime(), "Start time mismatch");
        assertEquals(null, sessionResponse.getEndTime(), "End time mismatch");

        var sessionResponse_2 = response[1];
        assertEquals(presentation_2_start, sessionResponse_2.getStartTime(), "Start time mismatch");
        assertEquals(presentation_2_end, sessionResponse_2.getEndTime(), "End time mismatch");
    }
}