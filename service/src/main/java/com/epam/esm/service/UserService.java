package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements com.epam.esm.service.Service<User> {

    private final UserDao userDao;

    private static final String USER = "User";

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll(int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }

        return userDao.getAll(limit, offset);
    }

    public User getById(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));
    }

    public long create(User user) {
        return userDao.create(user);
    }

    public void delete(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));

        userDao.delete(id);
    }
}
