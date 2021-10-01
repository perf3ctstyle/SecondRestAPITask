package com.epam.esm.mapper;

import com.epam.esm.constant.UserOrderConstants;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.util.DateTimeUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class UserOrderRowMapper implements RowMapper<UserOrder> {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Override
    public UserOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong(UserOrderConstants.ID);
        Long userId = rs.getLong(UserOrderConstants.USER_ID);
        Long giftCertificateId = rs.getLong(UserOrderConstants.GIFT_CERTIFICATE_ID);
        Integer cost = rs.getInt(UserOrderConstants.COST);

        String purchaseTimestampString = rs.getString(UserOrderConstants.PURCHASE_TIMESTAMP);
        LocalDateTime purchaseTimestamp = DateTimeUtils.of(purchaseTimestampString, DATE_TIME_PATTERN);

        return new UserOrder(id, userId, giftCertificateId, cost, purchaseTimestamp);
    }
}
