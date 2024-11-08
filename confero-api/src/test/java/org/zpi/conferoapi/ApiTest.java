package org.zpi.conferoapi;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.PresentationRequest;
import org.openapitools.model.PresenterRequest;
import org.openapitools.model.SessionType;
import org.zpi.conferoapi.user.User;

import java.util.Arrays;
import java.util.Collections;



public class ApiTest extends IntegrationTestBase {


    @Test
    void sanity_check() {
        userRepository.save(User.builder().isAdmin(false).build());

        var presenter = new PresenterRequest("Some orcid", "Some email");
        var presentation = new PresentationRequest()
                .title("Some title")
                .description("Some description")
                .presenters(Collections.singletonList(presenter));


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
                .header("Authorization", EMAIL)
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

        // when missing body
        RestAssured
                .given()
                .header("Authorization", EMAIL)
                .contentType("application/json")
                .post("/api/application")
                .then()
                .log().ifError()
                .statusCode(400);
    }
}
