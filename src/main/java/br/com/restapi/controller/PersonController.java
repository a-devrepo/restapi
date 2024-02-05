package br.com.restapi.controller;

import br.com.restapi.service.PersonService;
import br.com.restapi.vo.v1.PersonVO;
import br.com.restapi.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
@RestController
@RequestMapping("/api/person/v1")
@Tag(name="People", description= "Endpoints for Managing People")
public class PersonController {
    private final AtomicLong counter = new AtomicLong();
    private PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML})
    @Operation(summary = "Finds all people", description = "Find all people", tags = {"People"}
    ,responses = {
            @ApiResponse(description = "Success",responseCode = "200",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                    )}),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Not Found",responseCode = "404",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public List<PersonVO> findAll() {
        return service.findAll();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML})
    @Operation(summary = "Finds a person", description = "Finds a person", tags = {"People"}
            ,responses = {
            @ApiResponse(description = "Success",responseCode = "200",
                    content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                            )),
            @ApiResponse(description = "No Content",responseCode = "204",content = {@Content}),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Not Found",responseCode = "404",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public PersonVO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PatchMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML})
    @Operation(summary = "Disable a specific Person by your ID"
            , description = "Disable a specific Person by your ID", tags = {"People"}
            ,responses = {
            @ApiResponse(description = "Success",responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                    )),
            @ApiResponse(description = "No Content",responseCode = "204",content = {@Content}),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Not Found",responseCode = "404",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public PersonVO disablePerson(@PathVariable(value = "id") Long id) {
        return service.disablePerson(id);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML}
    )
    @Operation(summary = "Adds a new person", description = "Adds a new Person by passing" +
            "in a JSON, XML or YML representation of person", tags = {"People"}
            ,responses = {
            @ApiResponse(description = "Success",responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                    )),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public PersonVO create(@RequestBody PersonVO person) {
        return service.create(person);
    }

    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.APPLICATION_YML})
    @Operation(summary = "Updates a person", description = "Updates a Person by passing" +
            "in a JSON, XML or YML representation of person", tags = {"People"}
            ,responses = {
            @ApiResponse(description = "Updated",responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                    )),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Not Found",responseCode = "404",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public PersonVO update(@RequestBody PersonVO person) {
        return service.update(person);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a person", description = "Deletes a Person by passing " +
            "            in a JSON, XML or YML representation of person", tags = {"People"}
            ,responses = {
            @ApiResponse(description = "Success",responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                    )),
            @ApiResponse(description = "No Content",responseCode = "204",content = {@Content}),
            @ApiResponse(description = "Bad Request",responseCode = "400",content = {@Content}),
            @ApiResponse(description = "Unauthorized",responseCode = "401",content = {@Content}),
            @ApiResponse(description = "Not Found",responseCode = "404",content = {@Content}),
            @ApiResponse(description = "Internal Error",responseCode = "500",content = {@Content}),
    })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
