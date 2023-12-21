package br.com.restapi.service;

import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.model.Person;
import br.com.restapi.repository.PersonRepository;
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

    public List<Person> findAll() {
        logger.info("Finding all");
        return repository.findAll();
    }

    public Person create(Person person) {
        logger.info("Creating one person");
        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Updating one person");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());

        repository.save(entity);
        return entity;
    }

    public Person findById(Long id) {
        logger.info("Finding one person");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));
    }

    public void delete(Long id) {
        logger.info("Deleting one person");
        repository.deleteById(id);
    }
}
