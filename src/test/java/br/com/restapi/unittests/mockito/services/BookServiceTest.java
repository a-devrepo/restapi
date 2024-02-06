package br.com.restapi.unittests.mockito.services;


import br.com.restapi.exception.RequiredObjectIsNullException;
import br.com.restapi.model.Book;
import br.com.restapi.repository.BookRepository;
import br.com.restapi.service.BookService;
import br.com.restapi.unittests.mapper.mocks.MockBook;
import br.com.restapi.vo.v1.BookVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    MockBook input;

    @InjectMocks
    private BookService service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setup() throws Exception{
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById(){
        Book book = input.mockEntity(1);
        when(repository.findById(anyLong())).thenReturn(Optional.of(book));

        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Some Author1",result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals("Some Title1",result.getTitle());
        assertEquals(25.0,result.getPrice());

    }

    @Test
    void testCreate(){
        Book entity = input.mockEntity(1);
        Book persisted = entity;
        persisted.setId(1L);

        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.save(entity)).thenReturn(persisted);

        var result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Some Author1",result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals("Some Title1",result.getTitle());
        assertEquals(25D,result.getPrice());
    }

    @Test
    void testUpdate(){
        Book entity = input.mockEntity(1);
        Book persisted = entity;
        persisted.setId(1L);
        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);

        var result = service.update(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Some Author1",result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals("Some Title1",result.getTitle());
        assertEquals(25.0,result.getPrice());
    }

    @Test
    void testDelete(){
        service.delete(1L);

        verify(repository,times(1)).deleteById(1L);
    }

    @Test
    void testCreateWithNullObject(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
           service.create(null);
        });
        String expectedMessage = "It's not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateWithNullObject(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "It's not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /*@Test
    void testFindAll(){
        List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);

        var people = service.findAll(pageable);

        assertNotNull(people);
        assertEquals(14,people.size());

        var bookOne = people.get(1);

        assertNotNull(bookOne);
        assertNotNull(bookOne.getKey());
        assertNotNull(bookOne.getLinks());
        assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));

        var bookFour = people.get(4);

        assertNotNull(bookFour);
        assertNotNull(bookFour.getKey());
        assertNotNull(bookFour.getLinks());
        assertTrue(bookFour.toString().contains("links: [</api/book/v1/4>;rel=\"self\"]"));

        var bookSeven = people.get(7);

        assertNotNull(bookSeven);
        assertNotNull(bookSeven.getKey());
        assertNotNull(bookSeven.getLinks());
        assertTrue(bookSeven.toString().contains("links: [</api/book/v1/7>;rel=\"self\"]"));
    }*/
}