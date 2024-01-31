package br.com.restapi.integrationtests.controller.withyaml;

import br.com.restapi.configs.TestConfigs;
import br.com.restapi.integrationtests.controller.withyaml.mapper.YamlMapper;
import br.com.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.restapi.integrationtests.vo.AccountCredentialsVO;
import br.com.restapi.integrationtests.vo.TokenVO;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;
    private static YamlMapper objectMapper;

    @BeforeAll
    public static void setup() {
        objectMapper = new YamlMapper();
    }

    @Test
    @Order(1)
    void testSignIn() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("alison", "admin1234");

        tokenVO = given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class, objectMapper);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    void testRefresh() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("alison", "admin1234");

        var newTokenVO = given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("username", tokenVO.getUserName())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class, objectMapper);

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }
}