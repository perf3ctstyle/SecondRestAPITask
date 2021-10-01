package com.epam.esm.mapper;

import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.util.DateTimeUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class GiftCertificateRowMapper implements RowMapper<GiftCertificate> {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int i) throws SQLException {
        Long id = resultSet.getLong(GiftCertificateConstants.ID);
        String name = resultSet.getString(GiftCertificateConstants.NAME);
        String description = resultSet.getString(GiftCertificateConstants.DESCRIPTION);
        Integer price = resultSet.getInt(GiftCertificateConstants.PRICE);
        Long duration = resultSet.getLong(GiftCertificateConstants.DURATION);

        String createDateString = resultSet.getString(GiftCertificateConstants.CREATE_DATE);
        LocalDateTime createDate = DateTimeUtils.of(createDateString, DATE_TIME_PATTERN);

        String lastUpdateDateString = resultSet.getString(GiftCertificateConstants.LAST_UPDATE_DATE);
        LocalDateTime lastUpdateDate = DateTimeUtils.of(lastUpdateDateString, DATE_TIME_PATTERN);

        return new GiftCertificate(id, name, description, price, duration, createDate, lastUpdateDate);
    }
}
