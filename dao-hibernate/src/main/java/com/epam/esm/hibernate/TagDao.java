package com.epam.esm.hibernate;

import com.epam.esm.constant.TagConstants;
import com.epam.esm.dto.TagCostDto;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with {@link Tag} entities in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class TagDao implements Dao<Tag> {

    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID, NAME FROM TAG LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_BY_ID = "SELECT ID, NAME FROM TAG WHERE ID = :ID";
    private static final String GET_BY_NAME = "SELECT ID, NAME FROM TAG WHERE NAME = :NAME";
    private static final String GET_TAGS_WITH_COST_SUM_OF_ORDERS =
            " SELECT t.*, SUM(cost) costSum FROM tag t "
                    + " INNER JOIN gift_and_tag gt on t.id = gt.tag_id "
                    + " INNER JOIN user_order uo on gt.certificate_id = uo.gift_certificate_id "
                    + " WHERE uo.user_id = :USER_ID "
                    + " GROUP BY t.id ORDER BY costSum DESC LIMIT 1";
    private static final String CREATE = "INSERT INTO TAG(NAME) VALUES(:NAME)";
    private static final String DELETE = "DELETE FROM TAG WHERE ID = :ID";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";
    private static final String USER_ID = "USER_ID";

    @Autowired
    public TagDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link Tag} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link Tag} objects to return
     * @param offset - a number of {@link Tag} objects to skip when returning
     * @return a {@link List} of {@link Tag} objects.
     */
    public List<Tag> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, Tag.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    /**
     * Returns a {@link Tag} object from a database by its id or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param id - the {@link Tag} object's id that is to be retrieved from a database.
     * @return {@link Optional} with a {@link Tag} object if it was found in a database.
     */
    public Optional<Tag> getById(long id) {
        Tag result = (Tag) entityManager
                .createNativeQuery(GET_BY_ID, Tag.class)
                .setParameter(TagConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

    /**
     * Returns a {@link Tag} object from a database by its name or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param name - the {@link Tag} object's name that is to be retrieved from a database.
     * @return {@link Optional} with a {@link Tag} object if it was found in a database.
     */
    public Optional<Tag> getByName(String name) {
        Tag result = (Tag) entityManager
                .createNativeQuery(GET_BY_NAME, Tag.class)
                .setParameter(TagConstants.NAME, name)
                .getSingleResult();

        return Optional.of(result);
    }

    /**
     * Returns the most widely used tag of {@link com.epam.esm.entity.User} with the highest cost of {@link com.epam.esm.entity.UserOrder}.
     *
     * @param userId - the {@link com.epam.esm.entity.User} id whose orders are to be used for searching.
     * @return {@link Optional} with a {@link TagCostDto} object if it was found in a database.
     */
    public Optional<TagCostDto> getMostWidelyUsedTagOfUserWithHighestCostOfOrders(long userId) {
        TagCostDto result = (TagCostDto) entityManager
                .createNativeQuery(GET_TAGS_WITH_COST_SUM_OF_ORDERS, TagCostDto.class)
                .setParameter(USER_ID, userId)
                .getSingleResult();

        return Optional.of(result);
    }

    /**
     * Creates a {@link Tag} object in a database.
     *
     * @param tag - the {@link Tag} object that is to be created in a database.
     * @return {@link Tag} object's id which was created in a database.
     */
    public long create(Tag tag) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager
                .createNativeQuery(CREATE)
                .setParameter(TagConstants.NAME, tag.getName());
        Tag createdTag = entityManager.merge(tag);
        transaction.commit();

        return createdTag.getId();
    }

    /**
     * Deletes a {@link Tag} object in a database by its id.
     *
     * @param id - the {@link Tag} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(TagConstants.ID, id)
                .executeUpdate();
    }
}
