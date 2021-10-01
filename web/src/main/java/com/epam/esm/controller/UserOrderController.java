package com.epam.esm.controller;

import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/userOrder")
public class UserOrderController {

    private final UserOrderService userOrderService;
    private final MessageSource messageSource;

    private static final String ID = "id";
    private static final String ID_PATH = "/{id}";

    private static final String JSON = "application/json";

    private static final int BAD_USER_ORDER_RECEIVED_CODE = 40004;
    private static final int USER_ORDER_NOT_FOUND_CODE = 40404;
    private static final int DAO_EXCEPTION_CODE = 50004;

    @Autowired
    public UserOrderController(UserOrderService userOrderService, MessageSource messageSource) {
        this.userOrderService = userOrderService;
        this.messageSource = messageSource;
    }

    @GetMapping(produces = JSON)
    public ResponseEntity<?> getUserOrders(@RequestParam(required = false) Long userId,
                                           @RequestParam int limit,
                                           @RequestParam int offset) {
        List<UserOrder> userOrders;

        if (userId == null) {
            userOrders = userOrderService.getAll(limit, offset);
        } else {
            userOrders = userOrderService.getAllByUserId(userId, limit, offset);
        }

        for (UserOrder userOrder : userOrders) {
            addGenericUserOrderLinks(userOrder);
        }

        Link link = linkTo(UserOrderController.class).withSelfRel();
        CollectionModel<UserOrder> result = CollectionModel.of(userOrders, link);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable(ID) long id) {
        UserOrder userOrder = userOrderService.getById(id);
        addGenericUserOrderLinks(userOrder);

        return new ResponseEntity<>(userOrder, HttpStatus.OK);
    }

    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody UserOrder userOrder) {
        userOrderService.create(userOrder);
        return new ResponseEntity<>(userOrder, HttpStatus.CREATED);
    }

    @DeleteMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> deleteById(@PathVariable(ID) long id) {
        userOrderService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<ErrorInfo> handleRequiredFieldsMissingException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.REQUIRED_FIELDS_MISSING, null, locale),
                BAD_USER_ORDER_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale),
                USER_ORDER_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorInfo> handleDaoException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.INTERNAL_ERROR, null, locale),
                DAO_EXCEPTION_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addGenericUserOrderLinks(UserOrder userOrder) {
        Link selfLink = linkTo(methodOn(UserOrderController.class).getById(userOrder.getId())).withSelfRel();
        userOrder.add(selfLink);
    }
}
