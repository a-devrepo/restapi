package br.com.restapi.integrationtests.controller.withxml;

import br.com.restapi.configs.TestConfigs;
import br.com.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.restapi.integrationtests.vo.AccountCredentialsVO;
import br.com.restapi.integrationtests.vo.BookVO;
import br.com.restapi.integrationtests.vo.TokenVO;
import br.com.restapi.integrationtests.vo.pagedmodels.PagedModelBook;
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
import java.util.GregorianCalendar;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static BookVO bookVO;

    @BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        bookVO = new BookVO();
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
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws IOException {
        mockBook();

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .body(bookVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getPrice());

        assertTrue(persistedBook.getId() > 0);

        assertEquals("J.R.R.Tolkien",persistedBook.getAuthor());
        assertEquals("Lord of The Rings",persistedBook.getTitle());
        assertEquals(new GregorianCalendar(1954,07,29).getTime(),persistedBook.getLaunchDate());
        assertEquals(70.00,persistedBook.getPrice());
    }

    @Test
    @Order(2)
    void testUpdate() throws IOException {
        bookVO.setTitle("LOTR - The Fellowship of the Ring");

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .body(bookVO)
                        .when()
                        .put()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        BookVO updatedBook = objectMapper.readValue(content, BookVO.class);

        assertNotNull(updatedBook);
        assertNotNull(updatedBook.getAuthor());
        assertNotNull(updatedBook.getTitle());
        assertNotNull(updatedBook.getLaunchDate());
        assertNotNull(updatedBook.getPrice());

        assertEquals(bookVO.getId(), updatedBook.getId());

        assertEquals("LOTR - The Fellowship of the Ring",updatedBook.getTitle());
        assertEquals("J.R.R.Tolkien",updatedBook.getAuthor());
        assertEquals(new GregorianCalendar(1954,07,29).getTime(),updatedBook.getLaunchDate());
        assertEquals(70.00,updatedBook.getPrice());
    }

    @Test
    @Order(3)
    void testFindById() throws IOException {

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .pathParam("id", bookVO.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .extract()
                        .body()
                        .asString();

        BookVO foundBook = objectMapper.readValue(content, BookVO.class);

        assertNotNull(foundBook);
        assertNotNull(foundBook.getAuthor());
        assertNotNull(foundBook.getTitle());
        assertNotNull(foundBook.getLaunchDate());
        assertNotNull(foundBook.getPrice());

        assertEquals(bookVO.getId(), foundBook.getId());

        assertEquals("LOTR - The Fellowship of the Ring",foundBook.getTitle());
        assertEquals("J.R.R.Tolkien",foundBook.getAuthor());
        assertEquals(new GregorianCalendar(1954,07,29).getTime(),foundBook.getLaunchDate());
        assertEquals(70.00,foundBook.getPrice());
    }

    @Test
    @Order(4)
    void testDelete() throws IOException {

                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .pathParam("id", bookVO.getId())
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 10, "direction", "asc")
                        .body(bookVO)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);

        var books = wrapper.getContent();

        BookVO foundBookOne = books.get(0);

        assertNotNull(foundBookOne);
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getLaunchDate());
        assertNotNull(foundBookOne.getPrice());

        assertTrue(foundBookOne.getId() > 0);

        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());
        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
        assertEquals(54.00, foundBookOne.getPrice());
    }

    @Test
    @Order(6)
    void testFindAllWithoutToken() throws IOException {

       RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
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

    @Test
    @Order(7)
    void testHateoas() throws IOException {

        var content =
                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 6, "direction", "asc")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/12</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/3</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/5</href></links>"));
        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=0&amp;size=6&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1?page=0&amp;size=6&amp;direction=asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=6&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=6&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=2&amp;size=6&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<page><size>6</size><totalElements>15</totalElements><totalPages>3</totalPages><number>0</number></page>"));

    }


    private void mockBook() {
        bookVO.setTitle("Lord of The Rings");
        bookVO.setAuthor("J.R.R.Tolkien");
        bookVO.setLaunchDate(new GregorianCalendar(1954,07,29).getTime());
        bookVO.setPrice(70.00);
    }
}