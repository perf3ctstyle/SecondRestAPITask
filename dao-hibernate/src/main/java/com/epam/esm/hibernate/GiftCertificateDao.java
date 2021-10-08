package com.epam.esm.hibernate;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.constructor.GiftCertificateQueryConstructor;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GiftCertificateDao {

    private final EntityManager entityManager;
    private final GiftCertificateQueryConstructor giftCertificateQueryConstructor;

    private static final String GET_ALL_WITH_PAGINATION = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_ALL_WITHOUT_PAGINATION = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE ";
    private static final String GET_BY_ID = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE WHERE ID = :ID";
    private static final String CREATE = "INSERT INTO GIFT_CERTIFICATE(NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE) VALUES(:NAME, :DESCRIPTION, :PRICE, :DURATION, :CREATE_DATE, :LAST_UPDATE_DATE)";
    private static final String UPDATE = "UPDATE GIFT_CERTIFICATE SET ";
    private static final String DELETE = "DELETE FROM GIFT_CERTIFICATE WHERE ID = :ID";

    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";
    private static final String ID_IN = " ID IN ";
    private static final String LIMIT = " LIMIT ";
    private static final String OFFSET = " OFFSET ";
    private static final String OPENING_BRACKET = " (";
    private static final String CLOSING_BRACKET = ") ";

    @Autowired
    public GiftCertificateDao(EntityManager entityManager, GiftCertificateQueryConstructor giftCertificateQueryConstructor) {
        this.entityManager = entityManager;
        this.giftCertificateQueryConstructor = giftCertificateQueryConstructor;
    }

    public List<GiftCertificate> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL_WITH_PAGINATION, GiftCertificate.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    public List<GiftCertificate> getGiftCertificates(SearchInfoDto searchInfoDto, String[] tagNames, int limit, int offset) {
        checkFieldExistence(searchInfoDto);

        StringBuilder stringBuilder = new StringBuilder(GET_ALL_WITHOUT_PAGINATION);

        String queryWithSearchInfo = SqlUtils.applyPartialSearch(searchInfoDto.getFieldAndValueForPartialSearch());
        if (!queryWithSearchInfo.isEmpty()) {
            stringBuilder
                    .append(WHERE)
                    .append(queryWithSearchInfo);
        }

        if (tagNames != null && tagNames.length > 0) {
            String queryWithTagsAndConditionConnection = giftCertificateQueryConstructor
                    .constructQueryForGettingIdsOfGiftCertificatesWithAndConditionConnectionToTags(tagNames);

            if (queryWithSearchInfo.isEmpty()) {
                stringBuilder.append(WHERE);
            } else {
                stringBuilder.append(AND);
            }

            stringBuilder
                    .append(ID_IN)
                    .append(OPENING_BRACKET)
                    .append(queryWithTagsAndConditionConnection)
                    .append(CLOSING_BRACKET);
        }

        String sortingQuery = SqlUtils.applySorting(searchInfoDto.getFieldForSorting(), searchInfoDto.getAscending());
        stringBuilder.append(sortingQuery);

        String paginationQuery = SqlUtils.applyPagination(limit, offset);
        stringBuilder.append(paginationQuery);

        return entityManager
                .createNativeQuery(stringBuilder.toString(), GiftCertificate.class)
                .getResultList();
    }

    public Optional<GiftCertificate> getById(long id) {
        GiftCertificate result = (GiftCertificate) entityManager
                .createNativeQuery(GET_BY_ID, GiftCertificate.class)
                .setParameter(GiftCertificateConstants.ID, id)
                .getSingleResult();

        return Optional.of(result);
    }

    public long create(GiftCertificate giftCertificate) {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager
                .createNativeQuery(CREATE)
                .setParameter(GiftCertificateConstants.NAME, giftCertificate.getName())
                .setParameter(GiftCertificateConstants.DESCRIPTION, giftCertificate.getDescription())
                .setParameter(GiftCertificateConstants.PRICE, giftCertificate.getPrice())
                .setParameter(GiftCertificateConstants.DURATION, giftCertificate.getDuration())
                .setParameter(GiftCertificateConstants.CREATE_DATE, giftCertificate.getCreateDate())
                .setParameter(GiftCertificateConstants.LAST_UPDATE_DATE, giftCertificate.getLastUpdateDate());
        GiftCertificate createdGiftCertificate = entityManager.merge(giftCertificate);
        transaction.commit();

        return createdGiftCertificate.getId();
    }

    public void update(long id, Map<String, String> fieldNameValueForUpdate) {
        String query = SqlUtils.constructQueryForUpdating(UPDATE, id, fieldNameValueForUpdate);
        entityManager
                .createNativeQuery(query)
                .executeUpdate();
    }

    public void delete(long id) {
        entityManager
                .createNativeQuery(DELETE)
                .setParameter(GiftCertificateConstants.ID, id)
                .executeUpdate();
    }

    private void checkFieldExistence(String fieldName, List<String> fields) {
        if (fieldName != null) {
            boolean doesFieldExist = false;

            for (String field : fields) {
                if (field.equalsIgnoreCase(fieldName)) {
                    doesFieldExist = true;
                    break;
                }
            }

            if (!doesFieldExist) {
                throw new IllegalArgumentException(fieldName + GenericExceptionMessageConstants.SUCH_FIELD_DOES_NOT_EXIST);
            }
        }
    }

    private void checkFieldExistence(SearchInfoDto searchInfoDto) {
        checkFieldExistence(searchInfoDto.getFieldForSorting(), GiftCertificateConstants.FIELDS);

        if (searchInfoDto.getFieldAndValueForPartialSearch() != null) {
            Set<String> fieldNames = searchInfoDto.getFieldAndValueForPartialSearch().keySet();

            for (String fieldName : fieldNames) {
                checkFieldExistence(fieldName, GiftCertificateConstants.FIELDS);
            }
        }
    }
}
