package org.zpi.conferoapi.organizer;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.OrganizerResponse;
import org.openapitools.model.SessionType;
import org.openapitools.model.SesssionEventResponse;
import org.springframework.http.HttpStatus;
import org.zpi.conferoapi.IntegrationTestBase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class OrganizerControllerTest extends IntegrationTestBase {

    @Test
    void findOrganizers() {
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
                "bar@gmail.com",
                "0000-0002-5678-1234",
                "Artsiom",
                "Shablinski",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        givenPresenter(
                "foo@gmail.com",
                "0000-0002-5678-1235",
                "ArtsiBar",
                "ShabFoo",
                "title 1",
                "Politechnika Wrocławska",
                true,
                presentation
        );

        givenUser(
                "0000-0002-5678-1239",
                "access-token1",
                "http://example.com/avatar.png",
                false,
                List.of("artsi@gmail.com")
        );


        Function<String, OrganizerResponse[]> searchForQuery = query -> RestAssured
                .given()
                .contentType("application/json")
                .header("Authorization", "artsi@gmail.com")
                .param("searchQuery", query)
                .get("/api/organizer")
                .then()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(OrganizerResponse[].class);

        assertEquals(1, searchForQuery.apply("Artsiom").length);
        assertEquals(2, searchForQuery.apply("Artsi").length);

        assertEquals(1, searchForQuery.apply("0000-0002-5678-1234").length);
        assertEquals(2, searchForQuery.apply("0000-0002").length);

        assertEquals(1, searchForQuery.apply("ShabFoo").length);
        assertEquals(2, searchForQuery.apply("Shab").length);
    }
}