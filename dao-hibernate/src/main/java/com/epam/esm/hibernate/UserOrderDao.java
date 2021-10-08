package com.epam.esm.hibernate;

import com.epam.esm.constant.UserOrderConstants;
import com.epam.esm.entity.UserOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class UserOrderDao {

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

    public List<UserOrder> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    public List<UserOrder> getAllByUserId(long userId, int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL_BY_USER_ID, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .setParameter(UserOrderConstants.USER_ID, userId)
                .getResultList();
    }

    public Optional<UserOrder> getById(long id) {
        UserOrder result = (UserOrder) entityManager
                .createNativeQuery(GET_BY_ID, UserOrder.class)
                .setParameter(UserOrderConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

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

    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(UserOrderConstants.ID, id)
                .executeUpdate();
    }
}
