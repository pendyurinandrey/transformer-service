package it.org.transformerservice.controllers;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.transformerservice.SpringBootApp;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@SpringBootTest(classes = SpringBootApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransformationControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @PostConstruct
    void postConstruct() {
        baseUrl = String.format("http://localhost:%d", port);
    }

    @Test
    public void testThatOneTransformerWillBeAppliedToOneElement() {
        var reqBody = """
                    {
                        "elements": [
                            {
                                "value": "123abcабц",
                                "transformers": [
                                    {
                                        "groupId": "translators",
                                        "transformerId": "cyrillic-and-greek"
                                    }
                                ]
                            }
                        ]
                    }
                """;
        given()
                .body(reqBody)
                .contentType("application/json")
                .when()
                .post(baseUrl + "/api/v1/transform")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("elements[0].sourceValue", equalTo("123abcабц"))
                .body("elements[0].transformedValue", equalTo("123abcabts"));
    }
}
