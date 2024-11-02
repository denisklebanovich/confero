package org.zpi.conferoapi;


import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected Integer port;

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

}
