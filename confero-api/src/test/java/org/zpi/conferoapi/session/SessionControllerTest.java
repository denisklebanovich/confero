package org.zpi.conferoapi.session;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SessionControllerTest extends IntegrationTestBase {

    @Test
    void getSessions() {
        var user = givenUser("0000-0002-5678-1234", "access-token", "http://example.com/avatar.png", false, List.of("artsi@gmail.com"));
        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, ChronoUnit.DAYS));
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
        assertEquals(false, sessionResponse.getIsInCalendar(), "Is in calendar mismatch");
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


    @Test
    void get_agenda() {

        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plusSeconds(3600));
        var session = givenSession(
                "Test Session",
                SessionType.SESSION,
                session_creator_user,
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
                session_creator_user,
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



        var agenda_requester = givenUser(
                "0000-0002-5678-1235",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );

        givenAgendaForUser(agenda_requester, List.of(session));

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/agenda")
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
        assertEquals(true, sessionResponse.getIsInCalendar(), "Is in calendar mismatch");
    }


    @Test
    void addRemoveSessionToFromAgenda() {

        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, ChronoUnit.DAYS));
        var session = givenSession(
                "Test Session",
                SessionType.SESSION,
                session_creator_user,
                conferenceEdition,
                "This is a test session description."
        );


        var presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                session,
                Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now().plus(2, ChronoUnit.HOURS)
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
                session_creator_user,
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


        givenUser(
                "0000-0002-5678-1235",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );

        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(response);
        assertEquals(0, response.length, "Expected one session in the response");


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new AddSessionToAgendaRequest().sessionId(session.getId()))
                .post("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());


        var response_1 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(response_1);
        assertEquals(1, response_1.length, "Expected one session in the response");


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new AddSessionToAgendaRequest().sessionId(session_2.getId()))
                .post("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());



        var response_2 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(response_2);
        assertEquals(2, response_2.length, "Expected one session in the response");


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new RemoveSessionFromAgendaRequest().sessionId(session_2.getId()))
                .delete("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value());


        var response_3 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/agenda")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(response_3);
        assertEquals(1, response_3.length, "Expected one session in the response");


        var allSessions = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionPreviewResponse[].class);

        assertNotNull(allSessions);
        assertEquals(2, allSessions.length, "Expected two sessions in the response");
        assertThat(allSessions).extracting(SessionPreviewResponse::getIsInCalendar).containsExactlyInAnyOrder(true, false);
    }


    @Test
    void findSession() {
        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, ChronoUnit.DAYS));
        var session = givenSession(
                "Test Session",
                SessionType.SESSION,
                session_creator_user,
                conferenceEdition,
                "This is a test session description."
        );


        var presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                session,
                Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now().plus(2, ChronoUnit.HOURS)
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

        givenUser(
                "0000-0002-5678-1235",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );

        givenUser(
                "0000-0002-5678-1236",
                "access-token2",
                "http://example.com/avatar.png",
                false,
                List.of("foobar@gmail.com")
        );

        var session_response_1 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .get("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionResponse.class);

        assertEquals(session.getId(), session_response_1.getId(), "Session ID mismatch");
        assertEquals(session.getTitle(), session_response_1.getTitle(), "Session title mismatch");
        assertEquals(true , session_response_1.getIsMine(), "Is mine mismatch");
        assertEquals(session.getDescription(), session_response_1.getDescription(), "Description mismatch");


        var session_response_2 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .get("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionResponse.class);


        assertEquals(session.getId(), session_response_2.getId(), "Session ID mismatch");
        assertEquals(session.getTitle(), session_response_2.getTitle(), "Session title mismatch");
        assertEquals(false , session_response_2.getIsMine(), "Is mine mismatch");
        assertEquals(session.getDescription(), session_response_2.getDescription(), "Description mismatch");
    }


}