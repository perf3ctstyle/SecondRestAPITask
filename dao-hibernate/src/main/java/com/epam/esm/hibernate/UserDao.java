package com.epam.esm.hibernate;

import com.epam.esm.audit.AuditableUser;
import com.epam.esm.constant.UserConstants;
import com.epam.esm.entity.User;
import com.epam.esm.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with {@link User} entities in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class UserDao implements Dao<User> {

    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID FROM USER LIMIT :LIMIT OFFSET :OFFSET";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";

    @Autowired
    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link User} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link User} objects to return
     * @param offset - a number of {@link User} objects to skip when returning
     * @return a {@link List} of {@link User} objects.
     */
    public List<User> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, User.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    /**
     * Returns a {@link User} object from a database by its id or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param id - the {@link User} object's id that is to be retrieved from a database.
     * @return {@link Optional} with a {@link User} object if it was found in a database.
     */
    public Optional<User> getById(long id) {
        User result = entityManager.find(User.class, id);
        return Optional.ofNullable(result);
    }

    /**
     * Creates a {@link User} object in a database.
     *
     * @param user - the {@link User} object that is to be created in a database.
     * @return {@link User} object's id which was created in a database.
     */
    public long create(User user) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager.persist(user);
        transaction.commit();

        return user.getId();
    }

    /**
     * Deletes a {@link User} object in a database.
     *
     * @param user - the {@link User} that is to be deleted in a database.
     */
    public void delete(User user) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager.remove(user);
        transaction.commit();
    }
}
