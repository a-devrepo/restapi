package br.com.restapi.service;

import br.com.restapi.controller.BookController;
import br.com.restapi.exception.RequiredObjectIsNullException;
import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.mapper.DozerMapper;
import br.com.restapi.model.Book;
import br.com.restapi.repository.BookRepository;
import br.com.restapi.vo.v1.BookVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {
    private Logger logger = Logger.getLogger(BookService.class.getName());

    private BookRepository repository;

    private PagedResourcesAssembler<BookVO> assembler;

    public BookService(BookRepository repository, PagedResourcesAssembler assembler) {
        this.assembler = assembler;
        this.repository = repository;
    }

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
        logger.info("Finding all");

        var bookPage = repository.findAll(pageable);
        var bookVOPage = bookPage.map(b -> DozerMapper.parseObject(b,BookVO.class));
        bookVOPage.map(b -> b.add(linkTo(methodOn(BookController.class)
                .findById(b.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(bookVOPage,link);
    }

    public BookVO create(BookVO book) {
        if(book == null) throw  new RequiredObjectIsNullException();
        logger.info("Creating one book");
        var entity = DozerMapper.parseObject(book, Book.class);
        entity = repository.save(entity);
        var vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(book.getKey())).withSelfRel());
        return vo;
    }

    public BookVO update(BookVO book) {
        if(book == null) throw  new RequiredObjectIsNullException();
        logger.info("Updating one book");
        Book entity = repository.findById(book.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        entity = repository.save(entity);
        var vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(book.getKey())).withSelfRel());
        return vo;
    }

    public BookVO findById(Long id) {
        logger.info("Finding one person");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
        BookVO vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person");
        repository.deleteById(id);
    }
}
