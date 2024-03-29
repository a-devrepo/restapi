package br.com.restapi.service;

import br.com.restapi.controller.PersonController;
import br.com.restapi.exception.RequiredObjectIsNullException;
import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.mapper.DozerMapper;
import br.com.restapi.model.Person;
import br.com.restapi.repository.PersonRepository;
import br.com.restapi.vo.v1.PersonVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    private PersonRepository repository;

    private PagedResourcesAssembler<PersonVO> assembler;

    public PersonService(PersonRepository repository, PagedResourcesAssembler<PersonVO> assembler) {
        this.assembler = assembler;
        this.repository = repository;
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all");

        var personPage  = repository.findAll(pageable);
        var personVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class)
                .findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(personVOPage,link) ;
    }

    public PagedModel<EntityModel<PersonVO>> findPersonsByName(Pageable pageable,String firstName) {
        logger.info("Finding all");

        var personPage  = repository.findPersonsByName(pageable,firstName);
        var personVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class)
                .findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(personVOPage,link) ;
    }

    public PersonVO create(PersonVO person) {
        if(person == null) throw  new RequiredObjectIsNullException();
        logger.info("Creating one person");
        var entity = DozerMapper.parseObject(person, Person.class);
        entity = repository.save(entity);
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if(person == null) throw  new RequiredObjectIsNullException();
        logger.info("Updating one person");
        Person entity = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());

        entity = repository.save(entity);
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }
    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Disabling one person");
        repository.disablePerson(id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person");
        repository.deleteById(id);
    }
}
