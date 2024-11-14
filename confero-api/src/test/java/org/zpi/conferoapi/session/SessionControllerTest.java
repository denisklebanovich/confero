package org.zpi.conferoapi.session;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.zpi.conferoapi.IntegrationTestBase;
import org.zpi.conferoapi.exception.ServiceException;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

class SessionControllerTest extends IntegrationTestBase {

    @Test
    void getSessions() {
        var user = givenUser("0000-0002-5678-1234", "access-token", "http://example.com/avatar.png", false, List.of("artsi@gmail.com"));
        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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
        assertNotNull(sessionResponse.getFromCurrentConferenceEdition(), "Current conference edition flag missing");
        assertEquals(true, sessionResponse.getFromCurrentConferenceEdition(), "Session is not from current conference edition");

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
        assertNull(sessionResponse.getStartTime(), "Start time mismatch");
        assertNull(sessionResponse.getEndTime(), "End time mismatch");

        var sessionResponse_2 = response[1];
        assertNotNull(sessionResponse_2.getStartTime(), "Start time mismatch");
        assertNotNull(sessionResponse_2.getEndTime(), "End time mismatch");
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


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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
    void get_agenda_returns_session_only_from_active_conference_edition() {
        var pastConferenceEdition = givenConferenceEdition(Instant.now().minus(2, DAYS));
        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));

        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );

        var past_session = givenSession(
                "Test Session",
                SessionType.SESSION,
                session_creator_user,
                pastConferenceEdition,
                "This is a test session description."
        );

        var past_presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                past_session,
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
                past_presentation
        );


        var current_session = givenSession(
                "Test Session",
                SessionType.SESSION,
                session_creator_user,
                conferenceEdition,
                "This is a test session description."
        );

        var current_presentation = givenPresentation(
                "Test Presentation",
                "Presentation Description",
                current_session,
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
                current_presentation
        );


        var agenda_requester = givenUser(
                "0000-0002-5678-1235",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );

        givenAgendaForUser(agenda_requester, List.of(current_session, past_session));

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


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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

        var presenter = givenPresenter(
                "artsi@gmail.com",
                "orcid1",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
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
        assertEquals(true, session_response_1.getIsMine(), "Is mine mismatch");
        assertEquals(session.getDescription(), session_response_1.getDescription(), "Description mismatch");
        assertEquals(sessionAttachments(session.getId()).size(), session_response_1.getPresentations().get(0).getAttachments().size(), "Attachments mismatch");

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
        assertEquals(false, session_response_2.getIsMine(), "Is mine mismatch");
        assertEquals(session.getDescription(), session_response_2.getDescription(), "Description mismatch");
    }

    @Test
    void updateSession() {
        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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

        var response_1 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(presentation.getId())
                                        .title("Updated Title")
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .extract()
                .response()
                .as(ErrorResponse.class);

        assertEquals(response_1.getReason(), ErrorReason.ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION);


        var presentationStart = Instant.now().plus(1, ChronoUnit.HOURS);
        var presentationEnd = Instant.now().plus(2, ChronoUnit.HOURS);

        var response_2 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(presentation.getId())
                                        .title("Updated Title")
                                        .description("Updated Description")
                                        .startTime(presentationStart)
                                        .endTime(presentationEnd)
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SessionResponse.class);
        assertEquals("Updated Title", response_2.getPresentations().get(0).getTitle(), "Presentation title mismatch");
        assertEquals("Updated Description", response_2.getPresentations().get(0).getDescription(), "Presentation description mismatch");
        assertEquals(presentationStart, response_2.getPresentations().get(0).getStartTime(), "Presentation start time mismatch");
        assertEquals(presentationEnd, response_2.getPresentations().get(0).getEndTime(), "Presentation end time mismatch");
        assertEquals(true, response_2.getIsMine(), "Is mine mismatch");


        var response_3 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(666L)
                                        .title("Updated Title")
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .response()
                .as(ErrorResponse.class);
        assertEquals(response_3.getReason(), ErrorReason.PRESENTATION_NOT_FOUND);


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(presentation.getId())
                                        .startTime(presentationStart)
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .response()
                .as(ErrorResponse.class);


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(presentation.getId())
                                        .endTime(presentationEnd)
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .response()
                .as(ErrorResponse.class);


        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .body(new UpdateSessionRequest()
                        .presentations(List.of(
                                new UpdatePresentationRequest()
                                        .id(presentation.getId())
                                        .endTime(presentationStart)
                                        .startTime(presentationEnd)
                        ))
                )
                .put("/api/session/" + session.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .response()
                .as(ErrorResponse.class);

    }


    @Test
    void addSessionsToAgendaByOrginizer() {

        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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

        var presenter = givenPresenter(
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


        var presenter_2 = givenPresenter(
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
                "0000-0002-5678-1230",
                "access-token123",
                "http://example.com/avatar.png",
                false,
                List.of("foobar@gmail.com")
        );

        givenAgendaForUser(agenda_requester, List.of(session));


        var response_1 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .post("/api/session/agenda/add-all-by-organizer/" + 999)
                .then()
                .log().ifError()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .response()
                .as(ErrorResponse.class);

        assertEquals(response_1.getReason(), ErrorReason.PRESENTER_NOT_FOUND);


        var response_2 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .post("/api/session/agenda/add-all-by-organizer/" + presenter.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(Integer.class);

        assertEquals(0, response_2, "Expected zero sessions to be added to the agenda");


        var response_3 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .post("/api/session/agenda/add-all-by-organizer/" + presenter_2.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(Integer.class);

        assertEquals(1, response_3, "Expected zero sessions to be added to the agenda");


        var response_4 = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .post("/api/session/agenda/add-all-by-organizer/" + presenter_2.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(Integer.class);

        assertEquals(0, response_4, "Expected zero sessions to be added to the agenda");

    }


    @Test
    void add_session_attachment() throws IOException {
        var session_creator_user = givenUser(
                "0000-0002-5678-1234",
                "access-token",
                "http://example.com/avatar.png",
                false,
                List.of("session-creator@gmail.com")
        );


        var conferenceEdition = givenConferenceEdition(Instant.now().plus(1, DAYS));
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
                "0000-0002-5678-1230",
                "access-token123",
                "http://example.com/avatar.png",
                false,
                List.of("foobar@gmail.com")
        );

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                MULTIPART_FORM_DATA_VALUE,
                "Mock File Content".getBytes()
        );

        var error_response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "foobar@gmail.com")
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", mockFile.getOriginalFilename(), mockFile.getInputStream(), mockFile.getContentType())
                .post("api/session/" + session.getId() + "/presentation/" + presentation.getId() + "/attachment")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .extract()
                .response()
                .as(ErrorResponse.class);

        assertEquals(error_response.getReason(), ErrorReason.ONLY_PARTICIPANT_CAN_UPDATE_PRESENTATION);


        givenUser(
                "0000-0002-5678-1232",
                "access-token1234",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );


        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", mockFile.getOriginalFilename(), mockFile.getInputStream(), mockFile.getContentType())
                .post("api/session/" + session.getId() + "/presentation/" + presentation.getId() + "/attachment")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(AttachmentResponse.class);

        assertEquals("test.pdf", response.getName(), "Attachment name mismatch");
        assertEquals("https://mock-s3-url.com/attachments/" + presentation.getId() + "/test.pdf", response.getUrl(), "Attachment URL mismatch");
        assertNotNull(response.getId(), "Attachment ID missing");
        assertThat(findAllAttachments().size()).isEqualTo(1);
    }


    @Test
    void delete_session_attachment() {
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

        var presenter = givenPresenter(
                "artsi@gmail.com",
                "orcid1",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        var attachment = givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenUser(
                "0000-0002-5678-1235",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );

        assertThat(findAllAttachments().size()).isEqualTo(1);

        RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .delete("api/session/" + session.getId() + "/presentation/" + presentation.getId() + "/attachment/" + attachment.getId())
                .then()
                .log().ifError()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(findAllAttachments().size()).isEqualTo(0);
    }


    @Test
    void getSessionFileUploadEvents() {
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

        var presenter = givenPresenter(
                "artsi@gmail.com",
                "orcid1",
                "name1",
                "surname1",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );

        givenAttachment(
                "attachmentUrl",
                "attachmentTitle",
                presenter
        );


        assertThat(findAllAttachments().size()).isEqualTo(6);


        var response = RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .param("pageSize", 4)
                .get("api/session/event")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(SesssionEventResponse[].class);

        assertThat(response.length).isEqualTo(4);

        var first_event = response[0];
        assertThat(first_event.getId()).isNotNull();
        assertThat(first_event.getType()).isEqualTo(SessionEventType.FILE_UPLOAD);
        assertThat(first_event.getTimestamp()).isNotNull();
        assertThat(first_event.getSessionId()).isEqualTo(session.getId());
        assertThat(first_event.getUserFirstName()).isEqualTo(presenter.getName());
        assertThat(first_event.getUserLastName()).isEqualTo(presenter.getSurname());
    }
}