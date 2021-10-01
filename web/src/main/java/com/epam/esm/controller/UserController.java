package com.epam.esm.controller;


import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.User;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import com.epam.esm.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    private static final String ID_PATH = "/{id}";

    private static final String JSON = "application/json";

    private static final int USER_NOT_FOUND_CODE = 40403;

    @Autowired
    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping(produces = JSON)
    public ResponseEntity<?> getAll(int limit, int offset) {
        List<User> users = userService.getAll(limit, offset);

        for (User user : users) {
            addGenericUserLinks(user);
        }

        Link link = linkTo(UserController.class).withSelfRel();
        CollectionModel<User> result = CollectionModel.of(users, link);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable long id) {
        User user = userService.getById(id);
        addGenericUserLinks(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody User user) {
        userService.create(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale),
                USER_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    private void addGenericUserLinks(User user) {
        Link selfLink = linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel();
        user.add(selfLink);
    }
}
