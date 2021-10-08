package com.epam.esm.hibernate;

import com.epam.esm.constant.TagConstants;
import com.epam.esm.dto.TagCostDto;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class TagDao {

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

    public List<Tag> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, Tag.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    public Optional<Tag> getById(long id) {
        Tag result = (Tag) entityManager
                .createNativeQuery(GET_BY_ID, Tag.class)
                .setParameter(TagConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

    public Optional<Tag> getByName(String name) {
        Tag result = (Tag) entityManager
                .createNativeQuery(GET_BY_NAME, Tag.class)
                .setParameter(TagConstants.NAME, name)
                .getSingleResult();

        return Optional.of(result);
    }

    public Optional<TagCostDto> getMostWidelyUsedTagOfUserWithHighestCostOfOrders(long userId) {
        TagCostDto result = (TagCostDto) entityManager
                .createNativeQuery(GET_TAGS_WITH_COST_SUM_OF_ORDERS, TagCostDto.class)
                .setParameter(USER_ID, userId)
                .getSingleResult();

        return Optional.of(result);
    }

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

    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(TagConstants.ID, id)
                .executeUpdate();
    }
}
