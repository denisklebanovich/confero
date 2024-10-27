package org.zpi.conferoapi;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.PresenterDto;
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
        PresenterDto presenter = new PresenterDto(
                "John",
                "Doe"
        );

        CreateApplicationRequest applicationWithMissingField = new CreateApplicationRequest(
                null,
                SessionType.WORKSHOP,
                Arrays.asList("Spring", "Boot", "Workshop"),
                "An in-depth workshop on Spring Boot features",
                Collections.singletonList(presenter),
                false
        );

        // when missing required field
        RestAssured
                .given()
                .contentType("application/json")
                .body(applicationWithMissingField)
                .post("application")
                .then()
                .log().ifError()
                .statusCode(400);

        CreateApplicationRequest validApplication = new CreateApplicationRequest(
                "some title",
                SessionType.WORKSHOP,
                Arrays.asList("Spring", "Boot", "Workshop"),
                "An in-depth workshop on Spring Boot features",
                Collections.singletonList(presenter),
                false
        );


        // when all required fields are present
        RestAssured
                .given()
                .contentType("application/json")
                .body(validApplication)
                .post("application")
                .then()
                .log().ifError()
                .statusCode(200);

        // when missing body
        RestAssured
                .given()
                .contentType("application/json")
                .post("application")
                .then()
                .log().ifError()
                .statusCode(400);
    }
}
