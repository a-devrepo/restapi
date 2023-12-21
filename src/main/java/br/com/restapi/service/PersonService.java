package br.com.restapi.service;

import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.repository.PersonRepository;
import br.com.restapi.vo.v1.PersonVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonVOService {
    private Logger logger = Logger.getLogger(PersonVOService.class.getName());

    private PersonRepository repository;

    public PersonVOService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<PersonVO> findAll() {
        logger.info("Finding all");
        return repository.findAll();
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person");
        return repository.save(person);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating one person");
        PersonVO entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());

        repository.save(entity);
        return entity;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
    }

    public void delete(Long id) {
        logger.info("Deleting one person");
        repository.deleteById(id);
    }
}
