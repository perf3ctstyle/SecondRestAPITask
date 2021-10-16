package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.User;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * This is a class that encapsulates the {@link User} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
public class UserService implements com.epam.esm.service.Service<User> {

    private final UserDao userDao;

    private static final String USER = "User";

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Returns {@link User} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link User} objects to return
     * @param offset - a number of {@link User} objects to skip when returning
     * @return a {@link List} of {@link User} objects.
     */
    public List<User> getAll(int limit, int offset) {
        checkPaginationParameters(limit, offset);
        return userDao.getAll(limit, offset);
    }

    /**
     * Returns a {@link User} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link User} object's id that is to be retrieved from a database.
     * @return {@link User} object.
     */
    public User getById(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));
    }

    /**
     * Creates a {@link User} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link ResourceAlreadyExistsException} if the User with the same name already exists.
     *
     * @param user - the {@link User} object that is to be created in a database.
     * @return {@link User} object's id which was created in a database.
     */
    public long create(User user) {
        return userDao.create(user);
    }

    /**
     * Deletes a {@link User} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link User} object's id that is to be deleted in a database.
     */
    @Transactional
    public void delete(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));

        userDao.delete(user);
    }
}
