package org.zpi.conferoapi;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.PresentationRequest;
import org.openapitools.model.PresenterRequest;
import org.openapitools.model.SessionType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfiguration.class, ConferoApiApplication.class}
)
public class ApiTest extends IntegrationTestBase {


    @Test
    void sanity_check() {
        var presenter = new PresenterRequest("Some orcid", "Some email");
        var presentation = new PresentationRequest("Some title", Collections.singletonList(presenter));


        CreateApplicationRequest applicationWithMissingField = new CreateApplicationRequest(
                null,
                SessionType.WORKSHOP,
                Arrays.asList("Spring", "Boot", "Workshop"),
                "An in-depth workshop on Spring Boot features",
                Collections.singletonList(presentation),
                false
        );

        // when missing required field
        RestAssured
                .given()
                .contentType("application/json")
                .body(applicationWithMissingField)
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(400);

        CreateApplicationRequest validApplication = new CreateApplicationRequest(
                "some title",
                SessionType.WORKSHOP,
                Arrays.asList("Spring", "Boot", "Workshop"),
                "An in-depth workshop on Spring Boot features",
                Collections.singletonList(presentation),
                false
        );

        // when all required fields are present
        RestAssured
                .given()
                .contentType("application/json")
                .body(validApplication)
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(200);

        // when missing body
        RestAssured
                .given()
                .contentType("application/json")
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(400);
    }
}
