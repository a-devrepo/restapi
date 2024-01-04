package br.com.restapi.service;

import br.com.restapi.controller.PersonController;
import br.com.restapi.exception.RequiredObjectIsNullException;
import br.com.restapi.exception.ResourceNotFoundException;
import br.com.restapi.mapper.DozerMapper;
import br.com.restapi.model.Person;
import br.com.restapi.repository.PersonRepository;
import br.com.restapi.repository.UserRepository;
import br.com.restapi.vo.v1.PersonVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService implements UserDetailsService {
    private Logger logger = Logger.getLogger(UserService.class.getName());

    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by name");
        var user = repository.findByUserName(username);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Username "+username + " not found");
        }
    }
}
