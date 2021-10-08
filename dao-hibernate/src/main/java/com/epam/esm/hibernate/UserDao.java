package com.epam.esm.hibernate;

import com.epam.esm.constant.UserConstants;
import com.epam.esm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID FROM USER LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_BY_ID = "SELECT ID FROM USER WHERE ID = :ID";
    private static final String CREATE = "INSERT INTO USER VALUES()";
    private static final String DELETE = "DELETE FROM USER WHERE ID = :ID";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";

    @Autowired
    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<User> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, User.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    public Optional<User> getById(long id) {
        User result = (User) entityManager
                .createNativeQuery(GET_BY_ID, User.class)
                .setParameter(UserConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

    public long create(User user) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager.createNativeQuery(CREATE);
        User createdUser = entityManager.merge(user);
        transaction.commit();

        return createdUser.getId();
    }

    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(UserConstants.ID, id)
                .executeUpdate();
    }
}
