package com.epam.esm.dao;

import com.epam.esm.entity.UserOrder;
import com.epam.esm.mapper.UserOrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserOrderDao implements Dao<UserOrder> {

    private final JdbcTemplate jdbcTemplate;
    private final UserOrderRowMapper userOrderRowMapper;

    private static final String GET_ALL = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER LIMIT ? OFFSET ?";
    private static final String GET_ALL_BY_USER_ID = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER WHERE USER_ID = ? LIMIT ? OFFSET ?";
    private static final String GET_BY_ID = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER WHERE ID = ?";
    private static final String CREATE = "INSERT INTO USER_ORDER(USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP) VALUES(?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM USER_ORDER WHERE ORDER_ID = ?";

    private static final int USER_ID_POSITION = 1;
    private static final int GIFT_CERTIFICATE_ID_POSITION = 2;
    private static final int COST_POSITION = 3;
    private static final int PURCHASE_TIMESTAMP_POSITION = 4;

    @Autowired
    public UserOrderDao(JdbcTemplate jdbcTemplate, UserOrderRowMapper userOrderRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userOrderRowMapper = userOrderRowMapper;
    }

    public List<UserOrder> getAll(int limit, int offset) {
        return jdbcTemplate.query(GET_ALL, userOrderRowMapper, limit, offset);
    }

    public List<UserOrder> getAllByUserId(long userId, int limit, int offset) {
        return jdbcTemplate.query(GET_ALL_BY_USER_ID, userOrderRowMapper, userId, limit, offset);
    }

    public Optional<UserOrder> getById(long id) {
        List<UserOrder> entities = jdbcTemplate.query(GET_BY_ID, userOrderRowMapper, id);
        return getSingleFoundEntity(entities);
    }

    public long create(UserOrder userOrder) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(USER_ID_POSITION, userOrder.getUserId());
            preparedStatement.setLong(GIFT_CERTIFICATE_ID_POSITION, userOrder.getGiftCertificateId());
            preparedStatement.setInt(COST_POSITION, userOrder.getCost());
            preparedStatement.setString(PURCHASE_TIMESTAMP_POSITION, userOrder.getPurchaseTimestamp().toString());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE, id);
    }
}
