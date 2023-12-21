package br.com.restapi.service;

import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.mapper.DozerMapper;
import br.com.restapi.model.Person;
import br.com.restapi.repository.PersonRepository;
import br.com.restapi.vo.v1.PersonVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    private PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<PersonVO> findAll() {
        logger.info("Finding all");
        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person");
        var entity = DozerMapper.parseObject(person, Person.class);
        entity = repository.save(entity);
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating one person");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());

        entity = repository.save(entity);
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting one person");
        repository.deleteById(id);
    }
}
