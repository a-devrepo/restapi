package br.com.restapi.integrationtests.controller.withjson;

import br.com.restapi.configs.TestConfigs;
import br.com.restapi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.restapi.integrationtests.vo.AccountCredentialsVO;
import br.com.restapi.integrationtests.vo.BookVO;
import br.com.restapi.integrationtests.vo.PersonVO;
import br.com.restapi.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO bookVO;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParam("id",bookVO.getId())
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
        mockBook();

                given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParam("id",bookVO.getId())
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
                        .body(bookVO)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        List<BookVO> books = objectMapper.readValue(content, new TypeReference<List<BookVO>>(){});

        BookVO foundBookOne = books.get(0);

        assertNotNull(foundBookOne);
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getLaunchDate());
        assertNotNull(foundBookOne.getPrice());

        assertTrue(foundBookOne.getId() > 0);

        assertEquals("Working effectively with legacy code",foundBookOne.getTitle());
        assertEquals("Michael C. Feathers",foundBookOne.getAuthor());
        assertEquals(49.00,foundBookOne.getPrice());
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .when()
                        .get()
                        .then()
                        .statusCode(403)
                        .extract()
                        .body()
                        .asString();
    }

    private void mockBook() {
        bookVO.setTitle("Lord of The Rings");
        bookVO.setAuthor("J.R.R.Tolkien");
        bookVO.setLaunchDate(new GregorianCalendar(1954,07,29).getTime());
        bookVO.setPrice(70.00);
    }
}