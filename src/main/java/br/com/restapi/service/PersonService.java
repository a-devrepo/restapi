package br.com.restapi.service;

import br.com.restapi.model.Person;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(String id) {
        logger.info("Finding one person");
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Alison");
        person.setLastName("Cruz");
        person.setAddress("Duque de Caxias");
        person.setGender("Male");
        return person;
    }
}
