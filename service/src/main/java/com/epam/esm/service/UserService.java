package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.hibernate.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        checkPaginationParameters(limit, offset);
        return userDao.getAll(limit, offset);
    }

    public User getById(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));
    }

    public long create(User user) {
        return userDao.create(user);
    }

    @Transactional
    public void delete(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));

        userDao.delete(id);
    }
}
