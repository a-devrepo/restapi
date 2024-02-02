package br.com.restapi.integrationtests.controller.withjson;

import br.com.restapi.configs.TestConfigs;
import br.com.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.restapi.integrationtests.vo.AccountCredentialsVO;
import br.com.restapi.integrationtests.vo.PersonVO;
import br.com.restapi.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonVO personVO;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        personVO = new PersonVO();
    }

    @Test
    @Order(0)
    void authorization() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("alison", "admin1234");

        String accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws IOException {
        mockPerson();

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(personVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
        personVO = createdPerson;

        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());

        assertTrue(createdPerson.getId() > 0);

        assertEquals("Richard",createdPerson.getFirstName());
        assertEquals("Stallman",createdPerson.getLastName());
        assertEquals("New York City, New York, US",createdPerson.getAddress());
        assertEquals("M",createdPerson.getGender());
    }

    @Test
    @Order(2)
    void testUpdate() throws IOException {
        personVO.setLastName("Stallman III");

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(personVO)
                        .when()
                        .put()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
        personVO = createdPerson;

        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());

        assertEquals(personVO.getId(), createdPerson.getId());

        assertEquals("Richard",createdPerson.getFirstName());
        assertEquals("Stallman III",createdPerson.getLastName());
        assertEquals("New York City, New York, US",createdPerson.getAddress());
        assertEquals("M",createdPerson.getGender());
    }

    @Test
    @Order(3)
    void testFindById() throws IOException {
        mockPerson();

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParam("id",personVO.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .extract()
                        .body()
                        .asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertEquals(personVO.getId(), persistedPerson.getId());

        assertEquals("Richard",persistedPerson.getFirstName());
        assertEquals("Stallman III",persistedPerson.getLastName());
        assertEquals("New York City, New York, US",persistedPerson.getAddress());
        assertEquals("M",persistedPerson.getGender());
    }

    @Test
    @Order(4)
    void testDelete() throws IOException {
        mockPerson();

                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParam("id",personVO.getId())
                        .when()
                        .delete("{id}")
                        .then()
                        .statusCode(204);
    }

    @Test
    @Order(5)
    void testFindAll() throws IOException {

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(personVO)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        List<PersonVO> people = objectMapper.readValue(content, new TypeReference<List<PersonVO>>(){});

        PersonVO foundPersonOne = people.get(0);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());

        assertTrue(foundPersonOne.getId() > 0);

        assertEquals("Nikola",foundPersonOne.getFirstName());
        assertEquals("Tesla",foundPersonOne.getLastName());
        assertEquals("Croatia",foundPersonOne.getAddress());
        assertEquals("Male",foundPersonOne.getGender());
    }

    @Test
    @Order(6)
    void testFindAllWithoutToken() throws IOException {

       RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

                given()
                        .spec(specificationWithoutToken)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .when()
                        .get()
                        .then()
                        .statusCode(403)
                        .extract()
                        .body()
                        .asString();
    }

    private void mockPerson() {
        personVO.setFirstName("Richard");
        personVO.setLastName("Stallman");
        personVO.setAddress("New York City, New York, US");
        personVO.setGender("M");
    }
}