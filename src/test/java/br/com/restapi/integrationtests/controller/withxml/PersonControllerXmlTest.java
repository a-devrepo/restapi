package br.com.restapi.integrationtests.controller.withxml;

import br.com.restapi.configs.TestConfigs;
import br.com.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.restapi.integrationtests.vo.AccountCredentialsVO;
import br.com.restapi.integrationtests.vo.PersonVO;
import br.com.restapi.integrationtests.vo.TokenVO;
import br.com.restapi.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.restapi.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static PersonVO personVO;

    @BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
        assertTrue(createdPerson.getEnabled());

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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
        assertTrue(createdPerson.getEnabled());

        assertEquals(personVO.getId(), createdPerson.getId());

        assertEquals("Richard",createdPerson.getFirstName());
        assertEquals("Stallman III",createdPerson.getLastName());
        assertEquals("New York City, New York, US",createdPerson.getAddress());
        assertEquals("M",createdPerson.getGender());
    }

    @Test
    @Order(3)
    void testFindById() throws IOException {
        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
        assertTrue(persistedPerson.getEnabled());

        assertEquals(personVO.getId(), persistedPerson.getId());

        assertEquals("Richard",persistedPerson.getFirstName());
        assertEquals("Stallman III",persistedPerson.getLastName());
        assertEquals("New York City, New York, US",persistedPerson.getAddress());
        assertEquals("M",persistedPerson.getGender());
    }

    @Test
    @Order(4)
    void testDisablePersonById() throws IOException {
        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .pathParam("id",personVO.getId())
                        .when()
                        .patch("{id}")
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
        assertFalse(persistedPerson.getEnabled());

        assertEquals(personVO.getId(), persistedPerson.getId());

        assertEquals("Richard",persistedPerson.getFirstName());
        assertEquals("Stallman III",persistedPerson.getLastName());
        assertEquals("New York City, New York, US",persistedPerson.getAddress());
        assertEquals("M",persistedPerson.getGender());
    }

    @Test
    @Order(5)
    void testDelete() throws IOException {
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .pathParam("id",personVO.getId())
                        .when()
                        .delete("{id}")
                        .then()
                        .statusCode(204);
    }

    @Test
    @Order(6)
    void testFindAll() throws IOException {
        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page",3, "size",10,"direction","asc")
                        .body(personVO)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
        var people = wrapper.getContent();

        PersonVO foundPersonOne = people.get(0);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());
        assertTrue(foundPersonOne.getEnabled());

        assertEquals(673,foundPersonOne.getId());
        assertEquals("Alic",foundPersonOne.getFirstName());
        assertEquals("Terbrug",foundPersonOne.getLastName());
        assertEquals("3 Eagle Crest Court",foundPersonOne.getAddress());
        assertEquals("Male",foundPersonOne.getGender());
    }

    @Test
    @Order(7)
    void testFindAllWithoutToken() throws IOException {

       RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

                given()
                        .spec(specificationWithoutToken)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
        personVO.setEnabled(true);
    }
}