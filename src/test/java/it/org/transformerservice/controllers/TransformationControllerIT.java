package it.org.transformerservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.transformerservice.SpringBootApp;
import org.transformerservice.dto.ElementDTO;
import org.transformerservice.dto.TransformRequestDTO;
import org.transformerservice.dto.TransformerConfigDTO;
import org.transformerservice.transformers.impl.MatchAndRemoveTransformer;
import org.transformerservice.transformers.impl.MatchAndReplaceTransformer;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@SpringBootTest(classes = SpringBootApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransformationControllerIT {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    public void testThat3ElementWillBeTransformed() throws Exception {
        var elements = List.of(
                new ElementDTO("шщзχψσ", List.of(
                        new TransformerConfigDTO("translators", "cyrillic-and-greek", null)
                )),
                new ElementDTO("AaBbCc", List.of(
                        new TransformerConfigDTO("matchers", "match-and-remove",
                                Map.of(MatchAndRemoveTransformer.REGEXP_PROPERTY_NAME, "[A-Z]"))
                )),
                new ElementDTO("02461 82 Needham St Newton Highlands", List.of(
                        new TransformerConfigDTO("matchers", "match-and-replace",
                                Map.of(MatchAndReplaceTransformer.REGEXP_PROPERTY_NAME, "^\\d{5}",
                                        MatchAndReplaceTransformer.REPLACEMENT_PROPERTY_NAME, "<Masked Postal Code>"))
                ))
        );
        var req = new TransformRequestDTO(elements);
        given()
                .body(objectMapper.writeValueAsString(req))
                .contentType("application/json")
                .when()
                .post(baseUrl + "/api/v1/transform")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("elements[0].sourceValue", equalTo("шщзχψσ"))
                .body("elements[0].transformedValue", equalTo("shshchzchpss"))
                .body("elements[1].sourceValue", equalTo("AaBbCc"))
                .body("elements[1].transformedValue", equalTo("abc"))
                .body("elements[2].sourceValue", equalTo("02461 82 Needham St Newton Highlands"))
                .body("elements[2].transformedValue", equalTo("<Masked Postal Code> 82 Needham St Newton Highlands"));
    }

    @Test
    public void testThat3TransformersWillBeAppliedTo1ElementInProperOrder() throws Exception {
        var element = new ElementDTO("itinit", List.of(
                new TransformerConfigDTO("matchers", "match-and-replace",
                        Map.of(MatchAndRemoveTransformer.REGEXP_PROPERTY_NAME, "it",
                                MatchAndReplaceTransformer.REPLACEMENT_PROPERTY_NAME, "ЧΛ")),
                new TransformerConfigDTO("translators", "cyrillic-and-greek", null),
                new TransformerConfigDTO("matchers", "match-and-remove",
                        Map.of(MatchAndRemoveTransformer.REGEXP_PROPERTY_NAME, "ChL$"))
        ));

        var req = new TransformRequestDTO(List.of(element));
        given()
                .body(objectMapper.writeValueAsString(req))
                .contentType("application/json")
                .when()
                .post(baseUrl + "/api/v1/transform")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("elements[0].sourceValue", equalTo("itinit"))
                .body("elements[0].transformedValue", equalTo("ChLin"));
    }

    @Test
    public void thatProperErrorResponseWillBeSentIfTransformerGroupIdIsUnknown() {
        var reqBody = """
                    {
                        "elements": [
                            {
                                "value": "123abcабц",
                                "transformers": [
                                    {
                                        "groupId": "wronggroup",
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
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("details", stringContainsInOrder("wronggroup", "cyrillic-and-greek"));
    }
}
