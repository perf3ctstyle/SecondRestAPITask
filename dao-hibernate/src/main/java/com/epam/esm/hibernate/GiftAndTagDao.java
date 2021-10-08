package com.epam.esm.hibernate;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with a {@link GiftCertificate} and {@link Tag}
 * shared table in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class GiftAndTagDao {

    private final EntityManager entityManager;

    private static final String GET_BY_CERTIFICATE_ID = "SELECT TAG_ID FROM GIFT_AND_TAG WHERE CERTIFICATE_ID = :CERTIFICATE_ID";
    private static final String GET_BY_TAG_ID = "SELECT CERTIFICATE_ID FROM GIFT_AND_TAG WHERE TAG_ID = :TAG_ID";
    private static final String CREATE = "INSERT INTO GIFT_AND_TAG(CERTIFICATE_ID, TAG_ID) VALUES (:CERTIFICATE_ID, :TAG_ID)";
    private static final String DELETE = "DELETE FROM GIFT_AND_TAG WHERE CERTIFICATE_ID = :CERTIFICATE_ID AND TAG_ID = :TAG_ID";

    private static final String CERTIFICATE_ID = "CERTIFICATE_ID";
    private static final String TAG_ID = "TAG_ID";

    @Autowired
    public GiftAndTagDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns a {@link List} of {@link Tag} ids which are connected to a certain {@link GiftCertificate}.
     *
     * @param certificateId - {@link GiftCertificate} id.
     * @return a {@link List} of {@link Tag} ids.
     */
    public List<Long> getTagIdsByCertificateId(long certificateId) {
        List<BigInteger> tagIds = entityManager
                .createNativeQuery(GET_BY_CERTIFICATE_ID)
                .setParameter(CERTIFICATE_ID, certificateId)
                .getResultList();

        return toLong(tagIds);
    }

    /**
     * Returns a {@link List} of {@link GiftCertificate} ids which are connected to a certain {@link Tag}.
     *
     * @param tagId - {@link Tag} id.
     * @return a {@link List} of {@link GiftCertificate} ids.
     */
    public List<Long> getCertificateIdsByTagId(Long tagId) {
        List<BigInteger> certificateIds = entityManager
                .createNativeQuery(GET_BY_TAG_ID)
                .setParameter(TAG_ID, tagId)
                .getResultList();

        return toLong(certificateIds);
    }

    /**
     * Creates a link between a {@link GiftCertificate} object and a {@link Tag} object.
     *
     * @param certificateId - {@link GiftCertificate} id.
     * @param tagId - {@link Tag} id.
     */
    public void create(Long certificateId, Long tagId) {
        entityManager
                .createNativeQuery(CREATE)
                .setParameter(CERTIFICATE_ID, certificateId)
                .setParameter(TAG_ID, tagId)
                .executeUpdate();
    }

    /**
     * Deletes a link between a {@link GiftCertificate} object and a {@link Tag} object.
     *
     * @param certificateId - {@link GiftCertificate} id.
     * @param tagId - {@link Tag} id.
     */
    public void delete(Long certificateId, Long tagId) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(CERTIFICATE_ID, certificateId)
                .setParameter(TAG_ID, tagId)
                .executeUpdate();
    }

    private List<Long> toLong(List<BigInteger> bigIntegers) {
        List<Long> result = new ArrayList<>();
        for (BigInteger bigInteger : bigIntegers) {
            result.add(bigInteger.longValue());
        }

        return result;
    }
}
