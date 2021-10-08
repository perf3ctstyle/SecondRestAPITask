package com.epam.esm.hibernate;

import com.epam.esm.constant.UserOrderConstants;
import com.epam.esm.entity.UserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with {@link UserOrder} entities in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class UserOrderDao implements Dao<UserOrder> {

    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_ALL_BY_USER_ID = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER WHERE USER_ID = :USER_ID LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_BY_ID = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER WHERE ID = :ID";
    private static final String CREATE = "INSERT INTO USER_ORDER(USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP) VALUES(:USER_ID, :GIFT_CERTIFICATE_ID, :COST, :PURCHASE_TIMESTAMP)";
    private static final String DELETE = "DELETE FROM USER_ORDER WHERE ID = :ID";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";

    @Autowired
    public UserOrderDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link UserOrder} objects to return
     * @param offset - a number of {@link UserOrder} objects to skip when returning
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering of a particular {@link com.epam.esm.entity.User}.
     *
     * @param userId - {@link com.epam.esm.entity.User} id
     * @param limit  - a number of {@link UserOrder} objects to return
     * @param offset - a number of {@link UserOrder} objects to skip when returning
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAllByUserId(long userId, int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL_BY_USER_ID, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .setParameter(UserOrderConstants.USER_ID, userId)
                .getResultList();
    }

    /**
     * Returns a {@link UserOrder} object from a database by its id or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param id - the {@link UserOrder} object's id that is to be retrieved from a database.
     * @return {@link Optional} with a {@link UserOrder} object if it was found in a database.
     */
    public Optional<UserOrder> getById(long id) {
        UserOrder result = (UserOrder) entityManager
                .createNativeQuery(GET_BY_ID, UserOrder.class)
                .setParameter(UserOrderConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

    /**
     * Creates a {@link UserOrder} object in a database.
     *
     * @param UserOrder - the {@link UserOrder} object that is to be created in a database.
     * @return {@link UserOrder} object's id which was created in a database.
     */
    public long create(UserOrder userOrder) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager
                .createNativeQuery(CREATE)
                .setParameter(UserOrderConstants.USER_ID, userOrder.getUserId())
                .setParameter(UserOrderConstants.GIFT_CERTIFICATE_ID, userOrder.getGiftCertificateId())
                .setParameter(UserOrderConstants.COST, userOrder.getCost())
                .setParameter(UserOrderConstants.PURCHASE_TIMESTAMP, userOrder.getPurchaseTimestamp());
        UserOrder createdUserOrder = entityManager.merge(userOrder);
        transaction.commit();

        return createdUserOrder.getId();
    }

    /**
     * Deletes a {@link UserOrder} object in a database by its id.
     *
     * @param id - the {@link UserOrder} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(UserOrderConstants.ID, id)
                .executeUpdate();
    }
}
