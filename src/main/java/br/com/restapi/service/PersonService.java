package br.com.restapi.service;

import br.com.restapi.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }
        return persons;
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet()+i);
        person.setFirstName("Alison");
        person.setLastName("Cruz");
        person.setAddress("Duque de Caxias");
        person.setGender("Male");
        return person;
    }

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
