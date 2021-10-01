package com.epam.esm.dao;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class GiftCertificateDao implements Dao<GiftCertificate> {

    private final JdbcTemplate jdbcTemplate;
    private final GiftCertificateRowMapper giftCertificateRowMapper;

    private static final String GET_ALL_WITH_PAGINATION = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE LIMIT ? OFFSET ?";
    private static final String GET_ALL_WITHOUT_PAGINATION = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE";
    private static final String GET_BY_ID = "SELECT ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE FROM GIFT_CERTIFICATE WHERE ID = ?";
    private static final String CREATE = "INSERT INTO GIFT_CERTIFICATE(NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE GIFT_CERTIFICATE SET ";
    private static final String DELETE = "DELETE FROM GIFT_CERTIFICATE WHERE ID=?";

    private static final int NAME_POSITION = 1;
    private static final int DESCRIPTION_POSITION = 2;
    private static final int PRICE_POSITION = 3;
    private static final int DURATION_POSITION = 4;
    private static final int CREATE_DATE_POSITION = 5;
    private static final int LAST_UPDATE_DATE_POSITION = 6;

    @Autowired
    public GiftCertificateDao(JdbcTemplate jdbcTemplate, GiftCertificateRowMapper giftCertificateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.giftCertificateRowMapper = giftCertificateRowMapper;
    }

    public List<GiftCertificate> getAll(int limit, int offset) {
        return jdbcTemplate.query(GET_ALL_WITH_PAGINATION, giftCertificateRowMapper, limit, offset);
    }

    public List<GiftCertificate> getGiftCertificates(SearchInfoDto searchInfoDto) {
        checkFieldExistence(searchInfoDto.getFieldForSorting(), GiftCertificateConstants.FIELDS);

        if (searchInfoDto.getFieldAndValueForPartialSearch() != null) {
            Set<String> fieldNames = searchInfoDto.getFieldAndValueForPartialSearch().keySet();

            for (String fieldName : fieldNames) {
                checkFieldExistence(fieldName, GiftCertificateConstants.FIELDS);
            }
        }

        String query = SqlUtils.constructQueryForGettingWithPartialSearchAndSorting(
                GET_ALL_WITHOUT_PAGINATION,
                searchInfoDto.getFieldAndValueForPartialSearch(),
                searchInfoDto.getFieldForSorting(),
                searchInfoDto.getIsAscending());

        return jdbcTemplate.query(query, giftCertificateRowMapper);
    }

    public Optional<GiftCertificate> getById(long id) {
        List<GiftCertificate> entities = jdbcTemplate.query(GET_BY_ID, giftCertificateRowMapper, id);
        return getSingleFoundEntity(entities);
    }

    public long create(GiftCertificate giftCertificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(NAME_POSITION, giftCertificate.getName());
            preparedStatement.setString(DESCRIPTION_POSITION, giftCertificate.getDescription());
            preparedStatement.setInt(PRICE_POSITION, giftCertificate.getPrice());
            preparedStatement.setLong(DURATION_POSITION, giftCertificate.getDuration());
            preparedStatement.setString(CREATE_DATE_POSITION, giftCertificate.getCreateDate().toString());
            preparedStatement.setString(LAST_UPDATE_DATE_POSITION, giftCertificate.getLastUpdateDate().toString());

            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void update(long id, Map<String, String> fieldNameValueForUpdate) {
        String query = SqlUtils.constructQueryForUpdating(UPDATE, id, fieldNameValueForUpdate);
        jdbcTemplate.update(query);
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE, id);
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
}
